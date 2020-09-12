package io.github.wysohn.rapidframework3.core.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.github.wysohn.rapidframework3.core.inject.module.MainCommandsModule;
import io.github.wysohn.rapidframework3.core.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.core.interfaces.command.ITabCompleter;
import io.github.wysohn.rapidframework3.modules.MockMainModule;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ManagerCommandTest {

    private List<Module> moduleList = new LinkedList<>();

    @Before
    public void init() {
        moduleList.add(new MainCommandsModule("test"));
        moduleList.add(new MockMainModule());
    }

    @Test
    public void enable() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        ManagerCommand managerCommand = injector.getInstance(ManagerCommand.class);

        managerCommand.enable();
    }

    @Test
    public void onCommand_notFound() throws Exception {
        ICommandSender mockSender = mock(ICommandSender.class);

        Injector injector = Guice.createInjector(moduleList);
        ManagerCommand managerCommand = injector.getInstance(ManagerCommand.class);

        SubCommand spyCommand = new SubCommand.Builder(managerCommand.main(), "somecmd")
                .create();
        managerCommand.enable();
        managerCommand.addCommand(spyCommand);

        assertTrue(managerCommand.onCommand(mockSender, "nottest", "nottest", new String[]{
                "nottest"
        }));
    }

    @Test
    public void onTabComplete() throws Exception {
        ICommandSender mockSender = mock(ICommandSender.class);

        Injector injector = Guice.createInjector(moduleList);
        ManagerCommand managerCommand = injector.getInstance(ManagerCommand.class);

        managerCommand.enable();
        managerCommand.addCommand(new SubCommand.Builder(managerCommand.main(), "somecmd")
                .addTabCompleter(0, TabCompleters.EMPTY)
                .addTabCompleter(1, TabCompleters.NULL)
                .addTabCompleter(2, new ITabCompleter() {
                    @Override
                    public List<String> getCandidates(String part) {
                        return TabCompleters.list("abc", "opq", "bddb");
                    }

                    @Override
                    public List<String> getHint() {
                        return TabCompleters.list("hint");
                    }
                })
                .addTabCompleter(3, TabCompleters.simple("ddeeff", "ggrr", "gger"))
                .create());
        managerCommand.addCommand(new SubCommand.Builder(managerCommand.main(), "othercmd")
                .create());

        when(mockSender.hasPermission(anyVararg())).thenReturn(true);

        //empty subcommand shows ...
        assertEquals(TabCompleters.list("..."), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{}));

        //tab complete test
        assertEquals(TabCompleters.list("somecmd"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"s"}));
        assertEquals(TabCompleters.list("somecmd"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"so"}));

        //tab complete test
        assertEquals(TabCompleters.list("othercmd"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"o"}));
        assertEquals(TabCompleters.list("othercmd"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"ot"}));

        // hint of ENPTY
        assertEquals(TabCompleters.list(), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", ""}));
        // tab complete
        assertEquals(TabCompleters.list(), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1"}));

        // hint of NULL
        assertNull(managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", ""}));
        // tab complete of NULL
        assertNull(managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2"}));

        // hint of custom
        assertEquals(TabCompleters.list("hint"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", ""}));
        // tab complete of custom
        assertEquals(TabCompleters.list("abc", "opq", "bddb"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", "arg3"}));

        // hint of simple
        assertEquals(TabCompleters.list("ddeeff", "ggrr", "gger"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", "arg3", ""}));
        // tab complete of simple
        assertEquals(TabCompleters.list("ddeeff"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", "arg3", "dd"}));
        assertEquals(TabCompleters.list("ggrr", "gger"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", "arg3", "gg"}));
        assertEquals(TabCompleters.list("ggrr"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", "arg3", "ggr"}));
        assertEquals(TabCompleters.list("gger"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", "arg3", "gge"}));
    }

    class TempSender implements ICommandSender {
        @Override
        public void sendMessageRaw(boolean conversation, String... msg) {

        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public UUID getUuid() {
            return null;
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public boolean isConversing() {
            return false;
        }

        @Override
        public boolean hasPermission(String... permission) {
            return false;
        }
    }
}