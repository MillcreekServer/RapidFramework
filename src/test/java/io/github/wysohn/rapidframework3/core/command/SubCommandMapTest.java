package io.github.wysohn.rapidframework3.core.command;

import com.google.inject.*;
import io.github.wysohn.rapidframework4.core.command.SubCommand;
import io.github.wysohn.rapidframework4.core.command.SubCommandMap;
import io.github.wysohn.rapidframework4.core.language.DefaultLangs;
import io.github.wysohn.rapidframework4.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework4.core.main.PluginMain;
import io.github.wysohn.rapidframework4.interfaces.ICommandSender;
import io.github.wysohn.rapidframework4.interfaces.command.CommandAction;
import io.github.wysohn.rapidframework4.testmodules.MockMainModule;
import io.github.wysohn.rapidframework4.utils.DoubleChecker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import javax.inject.Named;
import java.util.*;
import java.util.function.Predicate;

public class SubCommandMapTest {

    SubCommandMap subCommandMap;
    PluginMain mockMain;
    ManagerLanguage mockLang;
    TempSender mockSender;
    DoubleChecker doubleChecker;

    private List<Module> moduleList;

    @Before
    public void init() {
        doubleChecker = Mockito.spy(new DoubleChecker());
        subCommandMap = new SubCommandMap("cmd", "test", doubleChecker);

        mockMain = Mockito.mock(PluginMain.class);
        mockLang = Mockito.mock(ManagerLanguage.class);
        mockSender = Mockito.mock(TempSender.class);

        Mockito.when(mockMain.lang()).thenReturn(mockLang);

        moduleList = new LinkedList<>();
        moduleList.add(new MockMainModule());
        moduleList.add(new AbstractModule() {
            @Provides
            ManagerLanguage managerLanguage() {
                return mockLang;
            }

            @Provides
            @Named("rootPermission")
            String rootPermission() {
                return "testPerm";
            }
        });
    }

    @Test
    public void clearCommands() {
        Injector injector = Guice.createInjector(moduleList);

        Map commands = Whitebox.getInternalState(subCommandMap, "commandList");
        Map aliases = Whitebox.getInternalState(subCommandMap, "aliasMap");

        Assert.assertEquals(0, commands.size());
        Assert.assertEquals(0, aliases.size());

        subCommandMap.register(new SubCommand.Builder("test")
                .withAlias("someTest")
                .create(injector));

        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(1, aliases.size());

        subCommandMap.clearCommands();

        Assert.assertEquals(0, commands.size());
        Assert.assertEquals(0, aliases.size());
    }

    @Test
    public void dispatch_alias() {
        Injector injector = Guice.createInjector(moduleList);

        // simple + alias test
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);
        CommandAction mockAction = Mockito.mock(CommandAction.class);
        SubCommand mockCommand = new SubCommand.Builder("test5")
                .withAlias("bbc")
                .action(mockAction)
                .create(injector);

        subCommandMap.register(mockCommand);
        subCommandMap.dispatch(mockLang, mockSender, "test5", "test5");
        subCommandMap.dispatch(mockLang, mockSender, "bbc", "bbc");

        Mockito.verify(mockAction, Mockito.times(2)).execute(Mockito.eq(mockSender), Mockito.any());
    }

    @Test
    public void dispatch_permission() {
        Injector injector = Guice.createInjector(moduleList);

        // permission denied
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(false);
        CommandAction mockAction = Mockito.mock(CommandAction.class);
        SubCommand mockCommand = new SubCommand.Builder("test55")
                .withAlias("bbc")
                .action(mockAction)
                .create(injector);

        subCommandMap.register(mockCommand);
        subCommandMap.dispatch(mockLang, mockSender, "test55", "test55");

        Mockito.verify(mockLang).sendMessage(Mockito.eq(mockSender), Mockito.eq(DefaultLangs.General_NotEnoughPermission));
    }

    private class TestPredicate implements Predicate<ICommandSender>{
        @Override
        public boolean test(ICommandSender sender) {
            return false;
        }
    }

    @Test
    public void dispatch_predicate() {
        Injector injector = Guice.createInjector(moduleList);

        // predicate failed
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);
        CommandAction mockAction = Mockito.mock(CommandAction.class);

        Predicate<ICommandSender> mockPredicate = Mockito.spy(new TestPredicate());

        SubCommand mockCommand = new SubCommand.Builder("test88")
                .withAlias("ccd")
                .addPredicate(mockPredicate)
                .action(mockAction)
                .create(injector);

        subCommandMap.register(mockCommand);
        subCommandMap.dispatch(mockLang, mockSender, "test88", "test88");

        Mockito.verify(mockPredicate).test(Mockito.eq(mockSender));
    }
    @Test
    public void dispatch_nArgs() {
        Injector injector = Guice.createInjector(moduleList);

        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);

        SubCommand mockSubCommand = Mockito.spy(new SubCommand.Builder("test23", 2)
                .withAlias("alias23")
                .action(((sender, args) -> true))
                .create(injector));

        subCommandMap.register(mockSubCommand);
        subCommandMap.dispatch(mockLang, mockSender, "alias23", "alias23 3 4");

        Mockito.verify(mockSubCommand).execute(Mockito.eq(mockSender),
                Mockito.eq("alias23"), Mockito.eq(new String[]{"3", "4"}));
    }

    @Test
    public void dispatch_nArgs_notMatch() {
        Injector injector = Guice.createInjector(moduleList);

        // num args not match
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);
        CommandAction mockAction = Mockito.mock(CommandAction.class);
        Mockito.when(mockAction.execute(Mockito.any(), Mockito.any())).thenReturn(true);

        SubCommand subCommand = new SubCommand.Builder("test23", 2)
                .action(mockAction)
                .create(injector);

        subCommandMap.register(subCommand);
        subCommandMap.dispatch(mockLang, mockSender, "test23", "test23");

        Mockito.verify(mockLang, Mockito.times(2)).sendRawMessage(Mockito.eq(mockSender), Mockito.any());
    }

    @Test
    public void dispatch_doubecheck() {
        Injector injector = Guice.createInjector(moduleList);

        UUID uuid = UUID.randomUUID();
        Mockito.when(mockSender.getUuid()).thenReturn(uuid);

        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);

        CommandAction mockAction = Mockito.mock(CommandAction.class);
        Mockito.when(mockAction.execute(Mockito.any(), Mockito.any())).thenReturn(true);

        SubCommand subCommand = new SubCommand.Builder("test23")
                .action(mockAction)
                .needDoubleCheck()
                .create(injector);

        subCommandMap.register(subCommand);
        Map<UUID, String> checking = Mockito.spy(new HashMap<>());
        Whitebox.setInternalState(subCommandMap, "checking", checking);

        subCommandMap.dispatch(mockLang, mockSender, "test23", "test23");
        Mockito.verify(checking).put(Mockito.eq(uuid), Mockito.eq("test23"));
        Mockito.verify(doubleChecker).init(Mockito.eq(uuid), Mockito.any(), Mockito.any());
        Mockito.verify(mockLang).sendMessage(Mockito.eq(mockSender), Mockito.eq(DefaultLangs.Command_DoubleCheck_Init));

        subCommandMap.dispatch(mockLang, mockSender, "test23", "test23");
        Assert.assertFalse(checking.containsKey(uuid));
        Mockito.verify(doubleChecker).confirm(Mockito.eq(uuid));
    }

    @Test
    public void dispatch_doubecheck_timeout() {
        Injector injector = Guice.createInjector(moduleList);

        doubleChecker = Mockito.spy(new DoubleChecker(0));
        subCommandMap = new SubCommandMap("cmd", "test", doubleChecker);
        Map<UUID, String> checking = Mockito.spy(new HashMap<>());
        Whitebox.setInternalState(subCommandMap, "checking", checking);

        UUID uuid = UUID.randomUUID();
        Mockito.when(mockSender.getUuid()).thenReturn(uuid);

        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);

        CommandAction mockAction = Mockito.mock(CommandAction.class);
        Mockito.when(mockAction.execute(Mockito.any(), Mockito.any())).thenReturn(true);

        SubCommand subCommand = Mockito.spy(new SubCommand.Builder("test23")
                .action(mockAction)
                .needDoubleCheck()
                .create(injector));

        subCommandMap.register(subCommand);

        subCommandMap.dispatch(mockLang, mockSender, "test23", "test23");
        Mockito.verify(checking).put(Mockito.eq(uuid), Mockito.eq("test23"));
        Mockito.verify(doubleChecker).init(Mockito.eq(uuid), Mockito.any(), Mockito.any());
        Mockito.verify(mockLang).sendMessage(Mockito.eq(mockSender), Mockito.eq(DefaultLangs.Command_DoubleCheck_Init));

        doubleChecker.close();
        Assert.assertFalse(checking.containsKey(uuid));
        Mockito.verify(doubleChecker, Mockito.times(2)).reset(uuid);
    }

    @Test
    public void dispatch_notFound() {
        // command not found
        Mockito.when(mockSender.hasPermission(Mockito.any(), Matchers.anyVararg())).thenReturn(true);

        subCommandMap.dispatch(mockLang, mockSender, "someCmd", "someCmd");

        Mockito.verify(mockLang).sendMessage(Mockito.eq(mockSender),
                Mockito.eq(DefaultLangs.General_NoSuchCommand), Mockito.any());
    }

    @Test
    public void getCommand() {
        Injector injector = Guice.createInjector(moduleList);

        subCommandMap.register(new SubCommand.Builder("test")
                .withAlias("someTest")
                .create(injector));

        Assert.assertNotNull(subCommandMap.getCommand("test"));
        Assert.assertNull(subCommandMap.getCommand("other"));
    }

    @Test
    public void register() {
        Injector injector = Guice.createInjector(moduleList);

        Map commands = Whitebox.getInternalState(subCommandMap, "commandList");
        Map aliases = Whitebox.getInternalState(subCommandMap, "aliasMap");

        subCommandMap.register(new SubCommand.Builder("test2")
                .withAlias("someTest2")
                .create(injector));

        Assert.assertEquals(1, commands.size());
        Assert.assertEquals(1, aliases.size());
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