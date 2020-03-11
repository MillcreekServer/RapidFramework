package io.github.wysohn.rapidframework2.bukkit.testutils;

import io.github.wysohn.rapidframework2.bukkit.main.AbstractBukkitPlugin;
import io.github.wysohn.rapidframework2.bukkit.main.BukkitPluginBridge;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.IPluginManager;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.TaskSupervisor;
import io.github.wysohn.rapidframework2.core.main.PluginBridge;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.lang.LanguageSession;
import io.github.wysohn.rapidframework2.tools.FileUtil;
import org.bukkit.command.PluginCommand;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PluginMainTestBuilder {
    private PluginBridge mockBridge;
    private Logger mockLogger;
    private AbstractFileSession mockFileSession;
    private IPluginManager mockPluginManager;
    private Message mockMessage;

    private PluginMain main;
    private BukkitPluginBridge core;

    private List<Supplier<String>> mockSubCommands = new LinkedList<>();
    private List<Consumer<PluginMainTestBuilder>> befores = new LinkedList<>();
    private List<Consumer<PluginMainTestBuilder>> afters = new LinkedList<>();
    private List<Function<PluginMainTestBuilder, Boolean>> expectations = new LinkedList<>();
    private PluginCommand mockCommand;
    private AbstractBukkitPlugin mockBukkit;
    private TaskSupervisor mockSupervisor;

    private void initMocks() {
        mockBridge = mock(PluginBridge.class);
        mockLogger = mock(Logger.class);
        mockFileSession = mock(AbstractFileSession.class);
        mockPluginManager = mock(IPluginManager.class);
        mockMessage = mock(Message.class);
        mockBukkit = Mockito.mock(AbstractBukkitPlugin.class);
        mockSupervisor = Mockito.mock(TaskSupervisor.class);

        when(mockFileSession.get(Mockito.anyString())).thenReturn(Optional.empty());
        when(mockMessage.getString()).thenReturn("SomeMessage");
        when(mockBukkit.getCommand(Mockito.anyString())).thenReturn(mockCommand);
        when(mockBukkit.getTaskSupervisor()).thenReturn(mockSupervisor);
    }

    private PluginMainTestBuilder(String mainCommand, String adminPerm, PluginMain.Manager... managers){
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
                .andChatManager(mockFileSession, (sender, str) -> str)
                .setMessageSender(() -> false)
                .withManagers(new SomeManager(PluginMain.Manager.FASTEST_PRIORITY))
                .withManagers(managers)
                .addLangs(SomeLang.values())
                .build();
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

    public TaskSupervisor getMockSupervisor() {
        return mockSupervisor;
    }

    public PluginMain getMain() {
        return main;
    }

    public BukkitPluginBridge getCore() {
        return core;
    }

    public PluginMainTestBuilder before(Consumer<PluginMainTestBuilder> consumer){
        befores.add(consumer);
        return this;
    }

    public PluginMainTestBuilder after(Consumer<PluginMainTestBuilder> consumer){
        afters.add(consumer);
        return this;
    }


    public PluginMainTestBuilder mockSubCommand(Supplier<String> subCommandSupplier){
        this.mockSubCommands.add(subCommandSupplier);
        return this;
    }

    public PluginMainTestBuilder expect(Function<PluginMainTestBuilder, Boolean> fn){
        this.expectations.add(fn);
        return this;
    }

    public int test(ICommandSender sender, boolean boolToCheck){
        int count = 0;

        befores.forEach(pluginMainTestBuilderConsumer -> pluginMainTestBuilderConsumer.accept(this));

        for(Supplier<String> mockSubCommand : mockSubCommands){
            String command = mockSubCommand.get();
            if(!main.comm().onCommand(sender,
                    main.comm().getMainCommand(),
                    main.comm().getMainCommand(),
                    command.split(" ")))
                throw new RuntimeException("Command "+command+" returned false.");
        }

        for(Function<PluginMainTestBuilder, Boolean> fn : expectations){
            if(fn.apply(this) == boolToCheck)
                count++;
        }

        afters.forEach(pluginMainTestBuilderConsumer -> pluginMainTestBuilderConsumer.accept(this));

        return count;
    }
}
