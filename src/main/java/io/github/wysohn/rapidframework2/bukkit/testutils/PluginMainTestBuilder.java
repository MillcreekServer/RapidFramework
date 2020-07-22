package io.github.wysohn.rapidframework2.bukkit.testutils;

import io.github.wysohn.rapidframework2.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework2.bukkit.main.BukkitPluginBridge;
import io.github.wysohn.rapidframework2.core.interfaces.ITaskSupervisor;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.IPluginManager;
import io.github.wysohn.rapidframework2.core.main.PluginBridge;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.lang.LanguageSession;
import io.github.wysohn.rapidframework2.tools.FileUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PluginMainTestBuilder {
    private final ExecutorService async = Executors.newSingleThreadExecutor();

    private PluginBridge mockBridge;
    private Plugin mockPlatform;
    private Logger mockLogger;
    private AbstractFileSession mockFileSession;
    private IPluginManager mockPluginManager;
    private Message mockMessage;

    private final PluginMain main;
    private BukkitPluginBridge core;

    private final List<Object> expectations = new LinkedList<>();
    private final List<Consumer<PluginMainTestBuilder>> befores = new LinkedList<>();
    private final List<Consumer<PluginMainTestBuilder>> afters = new LinkedList<>();
    private PluginCommand mockCommand;
    private AbstractBukkitPlugin mockBukkit;
    private ITaskSupervisor mockSupervisor;
    private PluginDescriptionFile mockDescription;

    private PluginMainTestBuilder(String mainCommand, String adminPerm, PluginMain.Manager... managers) {
        initMocks();

        main = PluginMain.Builder
                .prepare("TestPluginName",
                        "This is a description of test plugin",
                        mainCommand,
                        adminPerm,
                        mockBridge,
                        mockLogger,
                        FileUtil.join(new File("build"), "tmp"))
                .andConfigSession(mockFileSession)
                .andPluginSupervisor(mockPluginManager)
                .andLanguageSessionFactory(locale -> new LanguageSession(mockFileSession))
                .andTaskSupervisor(mockSupervisor)
                .setMessageSender(() -> false)
                .withManagers(new SomeManager(PluginMain.Manager.FASTEST_PRIORITY))
                .withManagers(managers)
                .addLangs(SomeLang.values())
                .build();
    }

    private void initMocks() {
        mockBridge = mock(PluginBridge.class);
        mockPlatform = mock(Plugin.class);
        mockDescription = mock(PluginDescriptionFile.class);
        mockLogger = mock(Logger.class);
        mockFileSession = mock(AbstractFileSession.class);
        mockPluginManager = mock(IPluginManager.class);
        mockMessage = mock(Message.class);
        mockBukkit = Mockito.mock(AbstractBukkitPlugin.class);
        mockSupervisor = Mockito.mock(ITaskSupervisor.class);

        when(mockBridge.getPlatform()).thenReturn(mockPlatform);
        when(mockFileSession.get(Mockito.anyString())).thenReturn(Optional.empty());
        when(mockMessage.getString()).thenReturn("SomeMessage");
        when(mockBukkit.getCommand(Mockito.anyString())).thenReturn(mockCommand);

        when(mockSupervisor.sync(any(Callable.class))).then(ans -> {
            Object r = ((Callable) ans.getArguments()[0]).call();
            return new SimpleFuture(r);
        });
        doAnswer(ans -> {
            ((Runnable) ans.getArguments()[0]).run();
            return null;
        }).when(mockSupervisor).sync(any(Runnable.class));

        when(mockSupervisor.async(any(Callable.class))).then(ans -> async.submit((Callable) ans.getArguments()[0]));
        doAnswer(ans -> {
            async.submit((Runnable) ans.getArguments()[0]);
            return null;
        }).when(mockSupervisor).async(any(Runnable.class));

        when(mockPlatform.getDescription()).thenReturn(mockDescription);
    }

    private PluginMainTestBuilder(String mainCommand, String adminPerm, Class<? extends BukkitPluginBridge> clazz) throws Exception {
        initMocks();

        File file = new File("build"+File.separator+"tmp");
        FileUtil.delete(file); //clean up before starting any test

        Constructor con = clazz.getConstructor(String.class,
                String.class,
                String.class,
                String.class,
                Logger.class,
                File.class,
                IPluginManager.class,
                AbstractBukkitPlugin.class);
        core = (BukkitPluginBridge) con.newInstance("TestPluginName",
                "This is a description of test plugin",
                mainCommand,
                adminPerm,
                mockLogger,
                file,
                mockPluginManager,
                mockBukkit);

        core.init();
        main = core.getMain();
    }

    public static PluginMainTestBuilder create(String mainCommand, String adminPerm, PluginMain.Manager... managers){
        return new PluginMainTestBuilder(mainCommand, adminPerm, managers);
    }

    public static PluginMainTestBuilder create(String mainCommand, String adminPerm, Class<? extends BukkitPluginBridge> clazz){
        try {
            return new PluginMainTestBuilder(mainCommand, adminPerm, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PluginBridge getMockBridge() {
        return mockBridge;
    }

    public Plugin getMockPlatform() {
        return mockPlatform;
    }

    public Logger getMockLogger() {
        return mockLogger;
    }

    public AbstractFileSession getMockFileSession() {
        return mockFileSession;
    }

    public IPluginManager getMockPluginManager() {
        return mockPluginManager;
    }

    public Message getMockMessage() {
        return mockMessage;
    }

    public PluginCommand getMockCommand() {
        return mockCommand;
    }

    public AbstractBukkitPlugin getMockBukkit() {
        return mockBukkit;
    }

//    public TaskSupervisor getMockSupervisor() {
//        return mockSupervisor;
//    }

    public PluginMain getMain() {
        return main;
    }

    public BukkitPluginBridge getCore() {
        return core;
    }

    public PluginMainTestBuilder before(Consumer<PluginMainTestBuilder> consumer) {
        befores.add(consumer);
        return this;
    }

    public PluginMainTestBuilder after(Consumer<PluginMainTestBuilder> consumer){
        afters.add(consumer);
        return this;
    }


    public PluginMainTestBuilder mockSubCommand(Supplier<String> subCommandSupplier) {
        this.expectations.add(subCommandSupplier);
        return this;
    }

    public PluginMainTestBuilder expect(Function<PluginMainTestBuilder, Boolean> fn) {
        this.expectations.add(fn);
        return this;
    }

    public void test(ICommandSender sender, boolean boolToCheck) {
        int index = 1;

        befores.forEach(pluginMainTestBuilderConsumer -> pluginMainTestBuilderConsumer.accept(this));

        for (Object expt : expectations) {
            if (expt instanceof Supplier) {
                Supplier<String> mockCommand = (Supplier<String>) expt;

                String command = mockCommand.get();
                if (!main.comm().onCommand(sender,
                        main.comm().getMainCommand(),
                        main.comm().getMainCommand(),
                        command.split(" ")))
                    throw new RuntimeException("Command " + command + " returned false.");
            } else if (expt instanceof Function) {
                Function<PluginMainTestBuilder, Boolean> fn = (Function<PluginMainTestBuilder, Boolean>) expt;

                if (fn.apply(this) != boolToCheck)
                    throw new RuntimeException("Did not meet [" + index + "]th expectation.");
                else
                    index++;
            }
        }

        afters.forEach(pluginMainTestBuilderConsumer -> pluginMainTestBuilderConsumer.accept(this));

        async.shutdown();
    }

    private static class SimpleFuture implements Future {
        private final Object r;

        public SimpleFuture(Object r) {
            this.r = r;
        }

        @Override
        public boolean cancel(boolean b) {
            return true;
        }

        @Override
        public boolean isCancelled() {
            return true;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            return r;
        }

        @Override
        public Object get(long l, @NotNull TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return r;
        }
    }
}
