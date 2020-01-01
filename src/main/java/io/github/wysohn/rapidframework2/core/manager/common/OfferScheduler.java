package io.github.wysohn.rapidframework2.core.manager.common;

import io.github.wysohn.rapidframework2.core.interfaces.plugin.TaskSupervisor;
import io.github.wysohn.rapidframework2.core.manager.player.IPlayerWrapper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OfferScheduler {
    private static final long WAITING_TIME_MILLIS = 10 * 1000L;

    private final TaskSupervisor taskSupervisor;

    private Map<UUID, Runnable> pendingOffers = new ConcurrentHashMap<>();
    private Map<UUID, Future<Void>> runningTasks = new ConcurrentHashMap<>();

    public OfferScheduler(TaskSupervisor taskSupervisor) {
        this.taskSupervisor = taskSupervisor;
    }

    /**
     * Send invitation to the player. Caller must manually invoke {@link #acceptOffer(IPlayerWrapper)}
     * and {@link #declineOffer(IPlayerWrapper)} in order to actually make player to accept or decline offer.
     * However, after waiting for 5 minutes, the offer will be automatically declined.
     * @param player the player to send offer
     * @param onProgress callback which returns the time left until timeout, periodically. Useful to send
     *                        reminder. Synchronous call.
     * @param onOfferAccept callback when a player accepts offer in time. Synchronous call.
     * @param onTimeout callback which will be invoked when 5 minutes are passed.  At this point, the offer
     *                       is already declined. Synchronous call.
     * @param masks the mask to indicate when onProgress should be called. This always should be in descending
     *              order. For example, the default masking is '180000, 60000, 30000, 5000, 4000, 3000, 2000, 1000,' and
     *              this these instruct the onProgreses to be called only at 180000 milliseconds, 60000 milliseconds,
     *              and so on.
     * @return ture if sent; false if already waiting for response.
     */
    public boolean sendOffer(IPlayerWrapper player,
                            ProgressCallback onProgress,
                            Runnable onOfferAccept,
                            Runnable onTimeout,
                            long... masks){
        if(runningTasks.containsKey(player.getUuid()))
            return false;

        Future<Void> future = taskSupervisor.runAsync(new Offer(player.getUuid(),
                System.currentTimeMillis() + WAITING_TIME_MILLIS,
                new ProgressCallback() {
                    int currentIndex = 0;

                    @Override
                    public void millis(long left) {
                        if (currentIndex >= masks.length)
                            return;

                        while (masks[currentIndex] >= left) {
                            try {
                                taskSupervisor.runSync(() -> {
                                    onProgress.millis(masks[currentIndex++]);
                                    return null;
                                }).get();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {

                            }
                        }
                    }
                },
                onTimeout));

        pendingOffers.put(player.getUuid(), onOfferAccept);
        runningTasks.put(player.getUuid(), future);

        return true;
    }

    public boolean sendOffer(IPlayerWrapper player,
                            ProgressCallback onProgressAsync,
                            Runnable onOfferAccept,
                            Runnable onTimeout){
        return sendOffer(player, onProgressAsync, onTimeout, onOfferAccept,
                180000, 60000, 30000, 5000, 4000, 3000, 2000, 1000);
    }

    public boolean acceptOffer(IPlayerWrapper player){
        Future<Void> future = runningTasks.remove(player.getUuid());
        if(future == null)
            return false;

        future.cancel(true);

        pendingOffers.computeIfPresent(player.getUuid(), ((uuid, runnable) -> {
            taskSupervisor.runSync(() -> {
                runnable.run();
                return null;
            });

            return runnable;
        }));

        return true;
    }

    public boolean declineOffer(IPlayerWrapper player){
        return runningTasks.remove(player.getUuid()) != null;
    }

    public interface ProgressCallback {
        void millis(long left);
    }

    private class Offer implements Callable<Void>{
        private final UUID playerUuid;
        private final long offerEnds;
        private final ProgressCallback callback;
        private final Runnable onTimeout;

        public Offer(UUID playerUuid, long offerEnds, ProgressCallback callback, Runnable onTimeout) {
            this.playerUuid = playerUuid;
            this.offerEnds = offerEnds;
            this.callback = callback;
            this.onTimeout = onTimeout;
        }

        @Override
        public Void call() throws Exception {
            try {
                while (System.currentTimeMillis() < offerEnds) {
                    callback.millis(offerEnds - System.currentTimeMillis());
                    Thread.sleep(50L);
                }
            } catch (InterruptedException e) {
                return null;
            } finally {
                runningTasks.remove(playerUuid);

                taskSupervisor.runSync(()->{
                    onTimeout.run();
                    return null;
                }).get();
            }
            
            return null;
        }
    }

//    public static void main(String[] ar){
//        ExecutorService sync = Executors.newCachedThreadPool();
//        ExecutorService async = Executors.newCachedThreadPool();
//
//        OfferScheduler manager = new OfferScheduler(new TaskSupervisor() {
//            @Override
//            public <T> Future<T> runAsync(Callable<T> callable) {
//                System.out.println("Scheduled async");
//                return async.submit(callable);
//            }
//
//            @Override
//            public <T> Future<T> runSync(Callable<T> callable) {
//                System.out.println("Scheduled sync");
//                return sync.submit(callable);
//            }
//        });
//
//        for(int i = 0; i < 5; i++){
//            manager.sendOffer(new IPlayerWrapper() {
//                @Override
//                public String getDisplayName() {
//                    return null;
//                }
//
//                @Override
//                public void sendMessage(String... msg) {
//
//                }
//
//                @Override
//                public Locale getLocale() {
//                    return null;
//                }
//
//                @Override
//                public boolean hasPermission(String... permissions) {
//                    return false;
//                }
//
//                @Override
//                public UUID getUuid() {
//                    return UUID.randomUUID();
//                }
//
//                @Override
//                public UUID getGroupUuid() {
//                    return null;
//                }
//            }, (left -> System.out.println(String.format("left: %.1f", (left/1000.0)))), ()-> System.out.println("End"));
//        }
//
//        async.shutdown();
//    }
}
