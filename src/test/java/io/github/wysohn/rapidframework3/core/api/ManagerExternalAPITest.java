package io.github.wysohn.rapidframework3.core.api;

import com.google.inject.Guice;
import com.google.inject.Module;
import io.github.wysohn.rapidframework3.core.inject.module.ExternalAPIModule;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.testmodules.MockGlobalPluginManager;
import io.github.wysohn.rapidframework3.testmodules.MockLoggerModule;
import io.github.wysohn.rapidframework3.testmodules.MockMainModule;
import io.github.wysohn.rapidframework3.utils.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class ManagerExternalAPITest {
    private List<Module> moduleList = new LinkedList<>();

    @Before
    public void init() {
        moduleList.add(new MockMainModule());
        moduleList.add(new MockLoggerModule());
        moduleList.add(new MockGlobalPluginManager());
        moduleList.add(new ExternalAPIModule(Pair.of("SomeOtherPlugin", SomeExternalAPISupport.class)));
    }

    @Test
    public void enable() throws Exception {
        ManagerExternalAPI api = Guice.createInjector(moduleList)
                .getInstance(ManagerExternalAPI.class);

        api.enable();
    }

    @Test
    public void load() throws Exception {
        ManagerExternalAPI api = Guice.createInjector(moduleList)
                .getInstance(ManagerExternalAPI.class);

        api.enable();
        api.load();
    }

    @Test
    public void disable() throws Exception {
        ManagerExternalAPI api = Guice.createInjector(moduleList)
                .getInstance(ManagerExternalAPI.class);

        api.enable();
        api.disable();
    }

    private static class SomeExternalAPISupport extends ExternalAPI {
        public SomeExternalAPISupport(PluginMain main, String pluginName) {
            super(main, pluginName);
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
}