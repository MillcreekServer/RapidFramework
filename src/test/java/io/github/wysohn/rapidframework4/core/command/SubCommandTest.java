package io.github.wysohn.rapidframework4.core.command;

import com.google.inject.*;
import io.github.wysohn.rapidframework4.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework4.core.main.PluginMain;
import io.github.wysohn.rapidframework4.interfaces.ICommandSender;
import io.github.wysohn.rapidframework4.interfaces.language.ILang;
import io.github.wysohn.rapidframework4.testmodules.MockMainModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Named;
import java.util.*;

public class SubCommandTest {
    PluginMain mockMain;
    ManagerLanguage mockLang;
    TempSender mockSender;
    private List<Module> moduleList;

    @Before
    public void init() {
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
    public void execute() {
        Injector injector = Guice.createInjector(moduleList);

        List<Object> values = new ArrayList<>();

        SubCommand cmd = new SubCommand.Builder("testcmd", 3)
                .withAlias("testalias")
                .withDescription(TempLang.Description, (sen, langman) -> {

                })
                .addUsage(TempLang.Usage1, (sen, langman) -> {

                })
                .addUsage(TempLang.Usage2, (sen, langman) -> {

                })
                .addArgumentMapper(0, ArgumentMappers.STRING)
                .addArgumentMapper(1, ArgumentMappers.INTEGER)
                .addArgumentMapper(2, ArgumentMappers.DOUBLE)
                .action(((sender, args) -> {
                    values.add(args.get(0).orElse("null"));
                    values.add(args.get(1).orElse(-1));
                    values.add(args.get(2).orElse(-1.0));
                    return true;
                }))
                .create(injector);

        Assert.assertTrue(cmd.execute(mockSender, "testcmd", new String[]{"abc", "243", "356.55"}));

        Assert.assertEquals(3, values.size());
        Assert.assertEquals("abc", values.get(0));
        Assert.assertEquals(243, values.get(1));
        Assert.assertEquals(356.55, values.get(2));
    }

    enum TempLang implements ILang {
        Description("This is description"),
        Usage1("This is usage1"),
        Usage2("This is usage2"),
        ;

        private final String[] eng;

        TempLang(String... eng) {
            this.eng = eng;
        }

        @Override
        public String[] getEngDefault() {
            return eng;
        }
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