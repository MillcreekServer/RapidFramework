package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.lang.Lang;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SubCommandTest {
    PluginMain mockMain;
    ManagerLanguage mockLang;
    TempSender mockSender;

    @Before
    public void init() {
        mockMain = Mockito.mock(PluginMain.class);
        mockLang = Mockito.mock(ManagerLanguage.class);
        mockSender = Mockito.mock(TempSender.class);

        Mockito.when(mockMain.lang()).thenReturn(mockLang);
    }

    @Test
    public void execute() {
        List<Object> values = new ArrayList<>();

        SubCommand cmd = new SubCommand.Builder(mockMain, "testcmd", 3)
                .withAlias("testalias")
                .withDescription(TempLang.Description, (sen, langman) -> {

                })
                .addUsage(TempLang.Usage1, (sen, langman) -> {

                })
                .addUsage(TempLang.Usage2, (sen, langman) -> {

                })
                .addArgumentMapper(0, ArgumentMapper.STRING)
                .addArgumentMapper(1, ArgumentMapper.INTEGER)
                .addArgumentMapper(2, ArgumentMapper.DOUBLE)
                .action(((sender, args) -> {
                    values.add(args.get(0).orElse("null"));
                    values.add(args.get(1).orElse(-1));
                    values.add(args.get(2).orElse(-1.0));
                    return true;
                }))
                .create();

        Assert.assertTrue(cmd.execute(mockSender, "testcmd", new String[]{"abc", "243", "356.55"}));

        Assert.assertEquals(3, values.size());
        Assert.assertEquals("abc", values.get(0));
        Assert.assertEquals(243, values.get(1));
        Assert.assertEquals(356.55, values.get(2));
    }

    enum TempLang implements Lang {
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