package io.github.wysohn.rapidframework2.core.manager.common;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPlayerWrapper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class OfferScheduler {
    private static final long WAITING_TIME_MILLIS = 5 * 60 * 1000L;

    private final TaskSupervisor taskSupervisor;
    private Map<UUID, Future<Void>> pendingOffers = new ConcurrentHashMap<>();

    public OfferScheduler(TaskSupervisor taskSupervisor) {
        this.taskSupervisor = taskSupervisor;
    }

    /**
     * Send invitation to the player. Caller must manually invoke {@link #acceptOffer(IPlayerWrapper)}
     * and {@link #declineOffer(IPlayerWrapper)} in order to actually make player to accept or decline offer.
     * However, after waiting for 5 minutes, the offer will be automatically declined.
     * @param player the player to send offer
     * @param onProgressAsync async callback which returns the time left until timeout, periodically. Useful to send
     *                        reminder.
     * @param onTimeoutAsync async callback which will be invoked when 5 minutes are passed.  At this point, the offer
     *                       is already declined.
     * @param masks the mask to indicate when onProgressAsync should be called. This always should be in descending
     *              order. For example, the default masking is '180000, 60000, 30000, 5000, 4000, 3000, 2000, 1000,' and
     *              this these instruct the onProgresesAsync to be called only at 180000 milliseconds, 60000 milliseconds,
     *              and so on.
     * @return
     */
    public Status sendOffer(IPlayerWrapper player,
                            ProgressCallback onProgressAsync,
                            Runnable onTimeoutAsync,
                            long... masks){
        if(pendingOffers.containsKey(player.getUuid()))
            return Status.WAITING;

        Future<Void> future = taskSupervisor.runAsync(new Offer(player.getUuid(),
                System.currentTimeMillis() + WAITING_TIME_MILLIS,
                new ProgressCallback() {
                    int currentIndex = 0;

                    @Override
                    public void asyncMillis(long left) {
                        if (currentIndex >= masks.length)
                            return;

                        if (masks[currentIndex] >= left) {
                            onProgressAsync.asyncMillis(masks[currentIndex++]);
                        }
                    }
                },
                onTimeoutAsync));

        pendingOffers.put(player.getUuid(), future);
        return Status.SENT;
    }

    public Status sendOffer(IPlayerWrapper player,
                            ProgressCallback onProgressAsync,
                            Runnable onTimeoutAsync){
        return sendOffer(player, onProgressAsync, onTimeoutAsync,
                180000, 60000, 30000, 5000, 4000, 3000, 2000, 1000);
    }

    public boolean acceptOffer(IPlayerWrapper player){
        Future<Void> future = pendingOffers.remove(player.getUuid());
        if(future == null)
            return false;

        future.cancel(true);

        return true;
    }

    public boolean declineOffer(IPlayerWrapper player){
        return pendingOffers.remove(player.getUuid()) != null;
    }

    private interface ProgressCallback {
        void asyncMillis(long left);
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
                    callback.asyncMillis(offerEnds - System.currentTimeMillis());
                    Thread.sleep(50L);
                }
            } catch (InterruptedException e) {
                return null;
            } finally {
                pendingOffers.remove(playerUuid);
                onTimeout.run();
            }
            
            return null;
        }
    }

    public enum Status{
        WAITING, SENT,

        ;
    }

    public interface TaskSupervisor{
        <T> Future<T> runAsync(Callable<T> callable);
        <T> Future<T> runSync(Callable<T> callable);
    }

//    public static void main(String[] ar){
//        ExecutorService service = Executors.newCachedThreadPool();
//        InvitationManager manager = new InvitationManager(new TaskSupervisor() {
//            @Override
//            public <T> Future<T> runAsync(Callable<T> callable) {
//                return service.submit(callable);
//            }
//
//            @Override
//            public <T> Future<T> runSync(Callable<T> callable) {
//                return service.submit(callable);
//            }
//        });
//
//        for(int i = 0; i < 5; i++){
//            manager.sendInvitation(new IPlayerWrapper() {
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
//            }, (left -> System.out.println(String.format("left: %.1f", (left/1000.0)))), ()-> System.out.println("End"),
//                    5000, 4500, 4000, 3500, 3000, 2500);
//        }
//
//        service.shutdown();
//    }
}
