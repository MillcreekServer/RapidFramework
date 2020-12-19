package io.github.wysohn.rapidframework3.bukkit.manager.message;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework3.core.message.MessageBuilder;
import io.github.wysohn.rapidframework3.core.player.AbstractPlayerWrapper;
import io.github.wysohn.rapidframework3.interfaces.message.IMessageSender;
import io.github.wysohn.rapidframework3.interfaces.plugin.ITaskSupervisor;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.testmodules.MockPluginDirectoryModule;
import io.github.wysohn.rapidframework3.testmodules.MockStorageFactoryModule;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class QueuedMessageManagerTest {

    private final ExecutorService async = Executors.newSingleThreadExecutor();

    private List<AbstractModule> moduleList = new LinkedList<>();
    private AbstractPlayerWrapper player;
    private IKeyValueStorage storage;
    private UUID uuid;
    private AbstractBukkitPlugin.IPlayerWrapper playerWrapper;
    private IMessageSender messageSender;

    @Before
    public void init() {
        player = mock(AbstractPlayerWrapper.class);
        uuid = UUID.randomUUID();
        playerWrapper = mock(AbstractBukkitPlugin.IPlayerWrapper.class);
        storage = mock(IKeyValueStorage.class);
        messageSender = mock(IMessageSender.class);

        when(player.getUuid()).thenReturn(uuid);
        when(playerWrapper.wrap(eq(uuid))).thenReturn(player);

        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockStorageFactoryModule(storage));
        moduleList.add(new AbstractModule() {
            @Provides
            AbstractBukkitPlugin.IPlayerWrapper wrapper() {
                return playerWrapper;
            }

            @Provides
            IMessageSender messageSender() {
                return messageSender;
            }

            @Provides
            ITaskSupervisor taskSupervisor() {
                return new ITaskSupervisor() {
                    @Override
                    public <V> Future<V> sync(Callable<V> callable) {
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public void sync(Runnable runnable) {
                        runnable.run();
                    }

                    @Override
                    public <V> Future<V> async(Callable<V> callable) {
                        return async.submit(callable);
                    }

                    @Override
                    public void async(Runnable runnable) {
                        async.submit(runnable);
                    }
                };
            }
        });
    }

    @Test
    public void testAcceptOffline() throws Exception {
        QueuedMessageManager manager = Guice.createInjector(moduleList)
                .getInstance(QueuedMessageManager.class);
        manager.enable();
        manager.load();

        List<List<String>> list = new ArrayList<>();
        when(storage.get(eq(uuid.toString()))).thenReturn(Optional.of(list));
        when(storage.getKeys(anyBoolean())).thenReturn(Collections.singleton(uuid.toString()));

        manager.accept(player, new String[]{
                "message11",
                "message12",
                "message13",
        });

        manager.accept(player, new String[]{
                "message21",
                "message22",
                "message23",
        });

        manager.accept(player, new String[]{
                "message31",
                "message32",
                "message33",
        });

        assertEquals(3, list.size());

        async.shutdown();
        async.awaitTermination(10, TimeUnit.SECONDS);
        verify(player, atLeast(1)).isOnline();
        verify(player, never()).sendMessageRaw(any());
    }

    @Test
    public void testAccept() throws Exception {
        QueuedMessageManager manager = Guice.createInjector(moduleList)
                .getInstance(QueuedMessageManager.class);
        manager.enable();
        manager.load();

        List<List<String>> list = new ArrayList<>();
        when(storage.get(eq(uuid.toString()))).thenReturn(Optional.of(list));
        when(storage.getKeys(anyBoolean())).thenReturn(Collections.singleton(uuid.toString()));
        doAnswer(invocation -> {
            List<List<String>> sublist = (List<List<String>>) invocation.getArguments()[1];
            if (sublist != null)
                sublist = new ArrayList<>(sublist);
            list.clear();
            if (sublist != null)
                list.addAll(sublist);
            return null;
        }).when(storage).put(eq(uuid.toString()), any());

        manager.accept(player, new String[]{
                "message11",
                "message12",
                "message13",
        });

        manager.accept(player, new String[]{
                "message21",
                "message22",
                "message23",
        });

        manager.accept(player, new String[]{
                "message31",
                "message32",
                "message33",
        });

        Thread.sleep(5000L);

        assertEquals(3, list.size());

        when(player.isOnline()).thenReturn(true);

        async.shutdown();
        async.awaitTermination(10, TimeUnit.SECONDS);

        verify(player, atLeast(1)).isOnline();

        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message11")
                .build()));
        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message12")
                .build()));
        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message13")
                .build()));
        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message21")
                .build()));
        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message22")
                .build()));
        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message23")
                .build()));
        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message31")
                .build()));
        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message32")
                .build()));
        verify(messageSender).send(eq(player), eq(MessageBuilder.forMessage("message33")
                .build()));
    }
}