package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginManager;
import io.github.wysohn.rapidframework2.core.manager.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework2.core.manager.command.ManagerCommand;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import io.github.wysohn.rapidframework2.core.manager.lang.Lang;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class PluginMainTest {
    private enum SomeLang implements Lang{
        Blah("This is Blah")
        ;

        private final String[] eng;

        SomeLang(String... eng) {
            this.eng = eng;
        }

        @Override
        public String[] getEngDefault() {
            return eng;
        }
    }

    private static class SomeManager extends PluginMain.Manager {
        public SomeManager(int loadPriority) {
            super(loadPriority);
        }

        @Override
        public void enable() throws Exception {

        }

        @Override
        public void load() throws Exception {

        }

        @Override
        public void disable() throws Exception {

        }
    }

    private Logger mockLogger;
    private AbstractFileSession mockFileSession;
    private IPluginManager mockPluginManager;
    private PluginMain.Manager spyManager;

    private PluginMain main;

    @Before
    public void init() {
        mockLogger = mock(Logger.class);
        mockFileSession = mock(AbstractFileSession.class);
        mockPluginManager = mock(IPluginManager.class);

        spyManager = spy(new SomeManager(PluginMain.Manager.FASTEST_PRIORITY));

        main = PluginMain.Builder
                .beginWith("test", "perm.mission", mockLogger)
                .andConfigSession(mockFileSession)
                .andPluginManager(mockPluginManager)
                .withManagers(spyManager)
                .withLangs(SomeLang.values())
                .build();
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
        SomeManager manager = main.getManager(SomeManager.class);
        assertNotNull(manager);

        PluginMain.Manager manager2 = main.getManager("SomeManager");
        assertNotNull(manager2);
        assertEquals(SomeManager.class, manager2.getClass());
    }

    @Test
    public void testEnable() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class);
        assertNotNull(manager);
        assertEquals(spyManager, manager);

        verify(spyManager).enable();
    }

    @Test
    public void testLoad() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class);
        assertNotNull(manager);
        assertEquals(spyManager, manager);

        verify(spyManager).load();
    }

    @Test
    public void testDisable() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class);
        assertNotNull(manager);
        assertEquals(spyManager, manager);

        verify(spyManager).disable();
    }
}