package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SubCommandMapTest {

    SubCommandMap subCommandMap;
    PluginMain mockMain;
    ManagerLanguage mockLang;
    TempSender mockSender;

    @Before
    public void init() {
        subCommandMap = new SubCommandMap();
        mockMain = Mockito.mock(PluginMain.class);
        mockLang = Mockito.mock(ManagerLanguage.class);
        mockSender = Mockito.mock(TempSender.class);

        Mockito.when(mockMain.lang()).thenReturn(mockLang);
    }

    @Test
    public void clearCommands() {
        Map commands = Whitebox.getInternalState(subCommandMap, "commandList");
        Map aliases = Whitebox.getInternalState(subCommandMap, "aliasMap");

        Assert.assertEquals(0, commands.size());
        Assert.assertEquals(0, aliases.size());

        subCommandMap.register(new SubCommand.Builder(mockMain, "test")
                .withAlias("someTest")
                .create());

        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(1, aliases.size());

        subCommandMap.clearCommands();

        Assert.assertEquals(0, commands.size());
        Assert.assertEquals(0, aliases.size());
    }

    @Test
    public void dispatch_alias() {
        // simple + alias test
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);
        CommandAction mockAction = Mockito.mock(CommandAction.class);
        SubCommand mockCommand = new SubCommand.Builder(mockMain, "test5")
                .withAlias("bbc")
                .action(mockAction)
                .create();

        subCommandMap.register(mockCommand);
        subCommandMap.dispatch(mockMain, mockSender, "test5");
        subCommandMap.dispatch(mockMain, mockSender, "bbc");

        Mockito.verify(mockAction, Mockito.times(2)).execute(Mockito.eq(mockSender), Mockito.any());
    }

    @Test
    public void dispatch_permission() {
        // permission denied
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(false);
        CommandAction mockAction = Mockito.mock(CommandAction.class);
        SubCommand mockCommand = new SubCommand.Builder(mockMain, "test55")
                .withAlias("bbc")
                .action(mockAction)
                .create();

        subCommandMap.register(mockCommand);
        subCommandMap.dispatch(mockMain, mockSender, "test55");

        Mockito.verify(mockLang).sendMessage(Mockito.eq(mockSender), Mockito.eq(DefaultLangs.General_NotEnoughPermission));
    }

    @Test
    public void dispatch_nArgs() {
        // num args not match
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);
        CommandAction mockAction = Mockito.mock(CommandAction.class);
        Mockito.when(mockAction.execute(Mockito.any(), Mockito.any())).thenReturn(true);

        SubCommand mockCommand = new SubCommand.Builder(mockMain, "test23", 2)
                .action(mockAction)
                .create();

        subCommandMap.register(mockCommand);
        subCommandMap.dispatch(mockMain, mockSender, "test23");

        Mockito.verify(mockLang).sendMessage(Mockito.eq(mockSender),
                Mockito.eq(DefaultLangs.Command_Format_Aliases), Mockito.any());
    }

    @Test
    public void dispatch_notFound() {
        // command not found
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);

        subCommandMap.dispatch(mockMain, mockSender, "someCmd");

        Mockito.verify(mockLang).sendMessage(Mockito.eq(mockSender),
                Mockito.eq(DefaultLangs.General_NoSuchCommand), Mockito.any());
    }

    @Test
    public void getCommand() {
        subCommandMap.register(new SubCommand.Builder(mockMain, "test")
                .withAlias("someTest")
                .create());

        Assert.assertNotNull(subCommandMap.getCommand("test"));
        Assert.assertNull(subCommandMap.getCommand("other"));
    }

    @Test
    public void register() {
        Map commands = Whitebox.getInternalState(subCommandMap, "commandList");
        Map aliases = Whitebox.getInternalState(subCommandMap, "aliasMap");

        subCommandMap.register(new SubCommand.Builder(mockMain, "test2")
                .withAlias("someTest2")
                .create());

        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(1, aliases.size());
    }

    class TempSender implements ICommandSender {
        @Override
        public void sendMessage(String... msg) {

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