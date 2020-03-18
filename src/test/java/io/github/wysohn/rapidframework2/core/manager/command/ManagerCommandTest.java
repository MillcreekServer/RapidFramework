package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ManagerCommandTest {
    PluginMain mockMain;
    ManagerLanguage mockLang;
    ManagerCommand managerCommand;
    TempSender mockSender;


    @Before
    public void init() throws Exception {
        mockMain = Mockito.mock(PluginMain.class);
        mockLang = Mockito.mock(ManagerLanguage.class);

        managerCommand = new ManagerCommand(0, "test");
        Whitebox.setInternalState(managerCommand, "main", mockMain);
        mockSender = Mockito.mock(TempSender.class);

        Mockito.when(mockMain.getRootPermission()).thenReturn("root");
        managerCommand.enable();

        Mockito.when(mockMain.lang()).thenReturn(mockLang);
    }

    @Test
    public void onCommand_notFound() throws Exception {
        SubCommand spyCommand = new SubCommand.Builder(mockMain, "somecmd")
                .create();
        managerCommand.enable();
        managerCommand.addCommand(spyCommand);

        Assert.assertTrue(managerCommand.onCommand(mockSender, "nottest", "nottest", new String[]{
                "nottest"
        }));
    }

    @Test
    public void onTabComplete() throws Exception {
        managerCommand.enable();
        managerCommand.addCommand(new SubCommand.Builder(mockMain, "somecmd")
                .addTabCompleter(0, TabCompleter.EMPTY)
                .addTabCompleter(1, TabCompleter.NULL)
                .addTabCompleter(2, new TabCompleter() {
                    @Override
                    public List<String> getCandidates(String part) {
                        return TabCompleter.list("abc", "opq", "bddb");
                    }

                    @Override
                    public List<String> getHint() {
                        return TabCompleter.list("hint");
                    }
                })
                .create());
        managerCommand.addCommand(new SubCommand.Builder(mockMain, "othercmd")
                .create());

        Mockito.when(mockSender.hasPermission(Mockito.anyVararg())).thenReturn(true);

        //empty subcommand shows ...
        Assert.assertEquals(TabCompleter.list("..."), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{}));

        //tab complete test
        Assert.assertEquals(TabCompleter.list("somecmd"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"s"}));
        Assert.assertEquals(TabCompleter.list("somecmd"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"so"}));

        //tab complete test
        Assert.assertEquals(TabCompleter.list("othercmd"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"o"}));
        Assert.assertEquals(TabCompleter.list("othercmd"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"ot"}));

        // hint of ENPTY
        Assert.assertEquals(TabCompleter.list("?"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", ""}));
        // tab complete
        Assert.assertEquals(TabCompleter.list(), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1"}));

        // hint of NULL
        Assert.assertNull(managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", ""}));
        // tab complete of NULL
        Assert.assertNull(managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2"}));

        // hint of custom
        Assert.assertEquals(TabCompleter.list("hint"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", ""}));
        // tab complete of custom
        Assert.assertEquals(TabCompleter.list("abc", "opq", "bddb"), managerCommand.onTabComplete(mockSender, "test", "",
                new String[]{"somecmd", "arg1", "arg2", "arg3"}));
    }

    class TempSender implements ICommandSender {
        @Override
        public void sendMessageRaw(String... msg) {

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
        public boolean hasPermission(String... permission) {
            return false;
        }
    }
}