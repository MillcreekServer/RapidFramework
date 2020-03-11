package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework2.bukkit.testutils.PluginMainTestBuilder;
import io.github.wysohn.rapidframework2.bukkit.testutils.SomeManager;
import io.github.wysohn.rapidframework2.core.manager.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework2.core.manager.command.ManagerCommand;
import io.github.wysohn.rapidframework2.core.manager.common.message.Message;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class PluginMainTest {
    private PluginMainTestBuilder mainTest;
    private PluginMain main;

    @Before
    public void init() throws Exception {
        mainTest = PluginMainTestBuilder.create();
        main = mainTest.getMain();

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
        main.lang().sendRawMessage(mainTest.getMockSender(), new Message[]{mainTest.getMockMessage()});

        Mockito.verify(mainTest.getMockSender()).sendMessageRaw(Mockito.eq("SomeMessage"));
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