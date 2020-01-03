package io.github.wysohn.rapidframework2.core.main;

import io.github.wysohn.rapidframework.utils.files.FileUtil;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPluginManager;
import io.github.wysohn.rapidframework2.core.manager.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework2.core.manager.command.ManagerCommand;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractFileSession;
import io.github.wysohn.rapidframework2.core.manager.config.ManagerConfig;
import io.github.wysohn.rapidframework2.core.manager.lang.Lang;
import io.github.wysohn.rapidframework2.core.manager.lang.LanguageSession;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

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
        private boolean enable;
        private boolean load;
        private boolean disable;

        public SomeManager(int loadPriority) {
            super(loadPriority);
        }

        @Override
        public void enable() throws Exception {
            enable = true;
        }

        @Override
        public void load() throws Exception {
            load = true;
        }

        @Override
        public void disable() throws Exception {
            disable = true;
        }

        public void reset() {
            enable = false;
            load = false;
            disable = false;
        }
    }

    private PluginBridge mockBridge;
    private Logger mockLogger;
    private AbstractFileSession mockFileSession;
    private IPluginManager mockPluginManager;

    private PluginMain main;

    @Before
    public void init() throws Exception {
        mockBridge = mock(PluginBridge.class);
        mockLogger = mock(Logger.class);
        mockFileSession = mock(AbstractFileSession.class);
        mockPluginManager = mock(IPluginManager.class);

        Mockito.when(mockFileSession.get(Mockito.anyString())).thenReturn(Optional.empty());

        main = PluginMain.Builder
                .prepare("CivilSimulator",
                        "All in one claim plugin",
                        "test",
                        "perm.mission",
                        mockBridge,
                        mockLogger,
                        FileUtil.join(new File("build"), "tmp"))
                .andConfigSession(mockFileSession)
                .andPluginSupervisor(mockPluginManager)
                .andLanguageSessionFactory(locale -> new LanguageSession(mockFileSession))
                .withManagers(new SomeManager(PluginMain.Manager.FASTEST_PRIORITY))
                .addLangs(SomeLang.values())
                .build();

        try{
            main.enable();
        }catch(Exception ex){

        }
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
    public void testEnable() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class).orElse(null);
        assertNotNull(manager);

        main.enable();
        assertTrue(manager.enable);

        manager.reset();
    }

    @Test
    public void testLoad() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class).orElse(null);
        assertNotNull(manager);

        main.load();
        assertTrue(manager.load);

        manager.reset();
    }

    @Test
    public void testDisable() throws Exception {
        SomeManager manager = main.getManager(SomeManager.class).orElse(null);
        assertNotNull(manager);

        main.disable();
        assertTrue(manager.disable);

        manager.reset();
    }
}