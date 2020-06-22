package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework2.bukkit.testutils.SomeLang;
import io.github.wysohn.rapidframework2.bukkit.testutils.SomeManager;
import io.github.wysohn.rapidframework2.core.interfaces.ITaskSupervisor;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.plugin.IPluginManager;
import io.github.wysohn.rapidframework2.core.manager.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework2.core.manager.command.ManagerCommand;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import io.github.wysohn.rapidframework2.core.manager.lang.LanguageSession;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import io.github.wysohn.rapidframework2.tools.FileUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PluginMainTest {
    private PluginBridge mockBridge;
    private Logger mockLogger;
    private AbstractFileSession mockFileSession;
    private IPluginManager mockPluginManager;
    private ICommandSender mockSender;
    private Message mockMessage;

    private PluginMain main;

    private void initMocks() {
        mockBridge = mock(PluginBridge.class);
        mockLogger = mock(Logger.class);
        mockFileSession = mock(AbstractFileSession.class);
        mockPluginManager = mock(IPluginManager.class);
        mockSender = mock(ICommandSender.class);
        mockMessage = mock(Message.class);

        when(mockFileSession.get(Mockito.anyString())).thenReturn(Optional.empty());
        when(mockMessage.getString()).thenReturn("SomeMessage");
    }

    @Before
    public void init() throws Exception {
        initMocks();

        main = PluginMain.Builder
                .prepare("TestPluginName",
                        "This is a description of test plugin",
                        "maincmd",
                        "admin.perm",
                        mockBridge,
                        mockLogger,
                        FileUtil.join(new File("build"), "tmp"))
                .andConfigSession(mockFileSession)
                .andPluginSupervisor(mockPluginManager)
                .andLanguageSessionFactory(locale -> new LanguageSession(mockFileSession))
                .andTaskSupervisor(new ITaskSupervisor() {
                    @Override
                    public <V> Future<V> sync(Callable<V> callable) {
                        return null;
                    }

                    @Override
                    public void sync(Runnable runnable) {

                    }

                    @Override
                    public <V> Future<V> async(Callable<V> callable) {
                        return null;
                    }

                    @Override
                    public void async(Runnable runnable) {

                    }
                })
                .setMessageSender(() -> false)
                .withManagers(new SomeManager(PluginMain.Manager.FASTEST_PRIORITY))
                .addLangs(SomeLang.values())
                .build();

        main.preload();
        main.enable();
    }

    @Test
    public void comm() {
        assertNotNull(main.comm());
        assertEquals(ManagerCommand.class, main.comm().getClass());
    }

    @Test
    public void conf() {
        assertNotNull(main.conf());
        assertEquals(ManagerConfig.class, main.conf().getClass());
    }

    @Test
    public void api() {
        assertNotNull(main.api());
        assertEquals(ManagerExternalAPI.class, main.api().getClass());
    }

    @Test
    public void lang() {
        assertNotNull(main.lang());
        assertEquals(ManagerLanguage.class, main.lang().getClass());
    }

    @Test
    public void testGetManager() {
        SomeManager manager = main.getManager(SomeManager.class).orElse(null);
        assertNotNull(manager);

        PluginMain.Manager manager2 = main.getManager("SomeManager").orElse(null);
        assertNotNull(manager2);
    }

    @Test
    public void testSetMessageSender(){
        main.lang().sendRawMessage(mockSender, new Message[]{mockMessage});

        Mockito.verify(mockSender).sendMessageRaw(Mockito.eq("SomeMessage"));
    }

    @Test
    public void testEnable() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class).orElse(null);
        assertNotNull(manager);

        main.enable();
        assertTrue(manager.isEnable());

        manager.reset();
    }

    @Test
    public void testLoad() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class).orElse(null);
        assertNotNull(manager);

        main.load();
        assertTrue(manager.isLoad());

        manager.reset();
    }

    @Test
    public void testDisable() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class).orElse(null);
        assertNotNull(manager);

        main.disable();
        assertTrue(manager.isDisable());

        manager.reset();
    }
}