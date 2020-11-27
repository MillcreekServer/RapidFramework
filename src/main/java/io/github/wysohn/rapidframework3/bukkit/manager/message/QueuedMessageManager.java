package io.github.wysohn.rapidframework3.bukkit.manager.message;

import io.github.wysohn.rapidframework3.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.core.main.Manager;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.message.IQueuedMessageConsumer;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.concurrent.Future;

@Singleton
public class QueuedMessageManager extends Manager implements IQueuedMessageConsumer {
    private final File pluginDir;
    private final IStorageFactory storageFactory;
    private final AbstractBukkitPlugin.IPlayerWrapper wrapper;
    private final ITaskSupervisor taskSupervisor;

    private IKeyValueStorage storage;
    private Future<Void> messageConsumerFuture;

    private boolean running = false;

    @Inject
    public QueuedMessageManager(@PluginDirectory File pluginDir,
                                IStorageFactory storageFactory,
                                AbstractBukkitPlugin.IPlayerWrapper wrapper,
                                ITaskSupervisor taskSupervisor) {
        this.pluginDir = pluginDir;
        this.storageFactory = storageFactory;
        this.wrapper = wrapper;
        this.taskSupervisor = taskSupervisor;
    }

    @Override
    public void enable() throws Exception {
        storage = storageFactory.create(pluginDir, "queuedMessages.yml");
        running = true;
    }

    @Override
    public void load() throws Exception {
        storage.reload();

        if(messageConsumerFuture != null)
            messageConsumerFuture.cancel(true);
        messageConsumerFuture = taskSupervisor.async(() -> {
            try{
                while(running){
                    tick();
                    Thread.sleep(1000L);
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        });
    }

    @Override
    public void disable() throws Exception {
        running = false;
    }

    @Override
    public void accept(ICommandSender sender, String[] strings) {
        List<List<String>> messages = storage.get(sender.getUuid().toString())
                .filter(List.class::isInstance)
                .map(List.class::cast)
                .orElseGet(ArrayList::new);
        messages.add(Arrays.asList(strings));
        storage.put(sender.getUuid().toString(), messages);
    }

    void tick() {
        storage.getKeys(false).stream()
                .map(UUID::fromString)
                .forEach(uuid -> Optional.of(uuid)
                        .map(wrapper::wrap)
                        .ifPresent(player -> {
                            if(!player.isOnline())
                                return;

                            List<List<String>> messages = storage.get(uuid.toString())
                                    .map(List.class::cast)
                                    .filter(list -> list.size() > 0)
                                    .orElse(null);

                            if(messages == null)
                                return;

                            taskSupervisor.sync(() -> player.sendMessageRaw(messages.get(0).toArray(new String[0])));
                            if(messages.size() > 1){
                                storage.put(uuid.toString(), messages.subList(1, messages.size()));
                            } else {
                                storage.put(uuid.toString(), null);
                            }
                        }));
    }

    @Override
    protected void finalize() throws Throwable {
        running = false;
        super.finalize();
    }
}
