package io.github.wysohn.rapidframework3.core.language;

import com.google.inject.Guice;
import com.google.inject.Module;
import io.github.wysohn.rapidframework3.core.inject.module.DecimalFormatModule;
import io.github.wysohn.rapidframework3.core.inject.module.LanguagesModule;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.interfaces.language.ILangSessionFactory;
import io.github.wysohn.rapidframework3.interfaces.message.IBroadcaster;
import io.github.wysohn.rapidframework3.modules.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ManagerLanguageTest {
    private final Date date = new Date(1592466358000L); // Thursday, June 18, 2020 7:45:58 AM GMT

    private List<Module> moduleList = new LinkedList<>();

    private ILangSessionFactory langSessionFactory;
    private IBroadcaster broadcaster;

    @Before
    public void init() {
        langSessionFactory = mock(ILangSessionFactory.class);
        broadcaster = mock(IBroadcaster.class);

        moduleList.add(new MockMainModule());
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockStorageFactoryModule());
        moduleList.add(new MockMessageSenderModule());
        moduleList.add(new LanguagesModule(TempLang.values()));
        moduleList.add(new MockBroadcasterModule());
    }

    @Test
    public void addDouble() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DoubleLang, (sen, langman) -> {
            langman.addDouble(1023.2);
        });
        assertEquals("value=1,023.2,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DoubleLang, (sen, langman) -> {
            langman.addDouble(5.2);
            langman.addDouble(993223.12);
        });
        assertEquals("value=5.2,993,223.12", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DoubleLang, (sen, langman) -> {
        });
        assertEquals("value=null,null", parsed);
    }

    @Test
    public void setDecimalFormat() {
        moduleList.add(new DecimalFormatModule("##.0"));
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        double value = 12131451.67754;

        String parse = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DecimalLang, (sen, langman) -> {
            langman.addDouble(value);
        });
        assertEquals("Number is 12131451.7.", parse);
    }

    @Test
    public void enable() {

    }

    @Test
    public void load() {

    }

    @Test
    public void disable() {

    }

    @Test
    public void addInteger() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.IntegerLang, (sen, langman) -> {
            langman.addInteger(1452);
        });
        assertEquals("value=1452,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.IntegerLang, (sen, langman) -> {
            langman.addInteger(6231);
            langman.addInteger(32905939);
        });
        assertEquals("value=6231,32905939", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.IntegerLang, (sen, langman) -> {
        });
        assertEquals("value=null,null", parsed);
    }

    @Test
    public void addString() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.StringLang, (sen, langman) -> {
            langman.addString("test");
        });
        assertEquals("value=test,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.StringLang, (sen, langman) -> {
            langman.addString("este");
            langman.addString("tetetet");
        });
        assertEquals("value=este,tetetet", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.StringLang, (sen, langman) -> {
            langman.addString(new String[]{"hehehe", "rtrtrt"});
        });
        assertEquals("value=hehehe,rtrtrt", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.StringLang, (sen, langman) -> {
        });
        assertEquals("value=null,null", parsed);
    }

    @Test
    public void addBoolean() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.BooleanLang, (sen, langman) -> {
            langman.addBoolean(true);
        });
        assertEquals("value=&atrue&f,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.BooleanLang, (sen, langman) -> {
            langman.addBoolean(false);
            langman.addBoolean(true);
        });
        assertEquals("value=&cfalse&f,&atrue&f", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.BooleanLang, (sen, langman) -> {
        });
        assertEquals("value=null,null", parsed);
    }

    @Test
    public void addLong() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.LongLang, (sen, langman) -> {
            langman.addLong(1452);
        });
        assertEquals("value=1452,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.LongLang, (sen, langman) -> {
            langman.addLong(6231);
            langman.addLong(32905939);
        });
        assertEquals("value=6231,32905939", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.LongLang, (sen, langman) -> {
        });
        assertEquals("value=null,null", parsed);
    }

    @Test
    public void addDate1() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateLang, (sen, langman) ->
                langman.addDate(date));
        assertEquals("value=6/18/20 7:45 AM", parsed);

        String parsed2 = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DateLang, (sen, langman) ->
                langman.addDate(date));
        assertEquals("value=20. 6. 18 오전 7:45", parsed2);
    }

    @Test
    public void addDate2() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateFormatLang, (sen, langman) -> {
            langman.addDate(date);
        });
        assertEquals("value=Thursday, June 18, 2020 7:45:58 AM UTC", parsed);

        String parsed2 = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DateFormatLang, (sen, langman) -> {
            langman.addDate(date);
        });
        assertEquals("value=2020년 6월 18일 목요일 오전 7시 45분 58초 UTC", parsed2);
    }

    @Test
    public void addDate2_1() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateFormatLangInvalid, (sen, langman) -> {
            langman.addDate(date);
        });
        assertEquals("value=?illegal 'date' format?", parsed);
    }

    @Test
    public void addDate3() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateFormatTimezoneLang, (sen, langman) -> {
            langman.addDate(date);
        });
        assertEquals("value=Thursday, June 18, 2020 4:45:58 PM GMT+09:00", parsed);

        String parsed2 = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DateFormatTimezoneLang, (sen, langman) -> {
            langman.addDate(date);
        });
        assertEquals("value=2020년 6월 18일 목요일 오후 4시 45분 58초 GMT+09:00", parsed2);
    }

    @Test
    public void addDate3_1() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateFormatTimezoneLangInvalid, (sen, langman) -> {
            langman.addDate(date);
        });
        // TimeZone.getTimeZone() returns GMT if invalid timezone is passed.
        assertEquals("value=Thursday, June 18, 2020 7:45:58 AM GMT", parsed);
    }

    @Test
    public void parse() {
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        ICommandSender mockSender = mock(ICommandSender.class);
        when(mockSender.getLocale()).thenReturn(Locale.KOREAN);

        String[] parsed = managerLanguage.parse(Locale.KOREAN, null, TempLang.VariationLang, (sen, langman) -> {
            langman.addDouble(1324.5);
            langman.addString("ho");
        });

        assertEquals(2, parsed.length);
        assertEquals("value=1,324.5,null,ho,null,null", parsed[0]);
        assertEquals("value2=null,null,null,null,null", parsed[1]);


        parsed = managerLanguage.parse(TempLang.VariationLang);

        assertEquals(2, parsed.length);
        assertEquals("value=null,null,null,null,null", parsed[0]);
        assertEquals("value2=null,null,null,null,null", parsed[1]);

        parsed = managerLanguage.parse(TempLang.VariationLang, (sen, langman) -> {
            langman.addString(new String[]{"abc", "efg"});
        });

        assertEquals(2, parsed.length);
        assertEquals("value=null,null,abc,null,null", parsed[0]);
        assertEquals("value2=null,null,efg,null,null", parsed[1]);


        parsed = managerLanguage.parse(mockSender, TempLang.VariationLang);

        assertEquals(2, parsed.length);
        assertEquals("value=null,null,null,null,null", parsed[0]);
        assertEquals("value2=null,null,null,null,null", parsed[1]);

        parsed = managerLanguage.parse(mockSender, TempLang.VariationLang, (sen, langman) -> {
            langman.addInteger(555);
            langman.addBoolean(false);
            langman.addBoolean(true);
            langman.addLong(8987);
        });

        assertEquals(2, parsed.length);
        assertEquals("value=null,555,null,&cfalse&f,8987", parsed[0]);
        assertEquals("value2=null,null,null,&atrue&f,null", parsed[1]);
    }

    private enum TempLang implements ILang {
        SomeLang("This is message."),
        DecimalLang("Number is ${double}."),

        DoubleLang("value=${double},${double}"),
        IntegerLang("value=${integer},${integer}"),
        StringLang("value=${string},${string}"),
        BooleanLang("value=${boolean},${boolean}"),
        LongLang("value=${long},${long}"),
        DateLang("value=${date}"),
        DateFormatLang("value=${date full}"),
        DateFormatLangInvalid("value=${date blah}"),
        DateFormatTimezoneLang("value=${date full GMT+09:00}"),
        DateFormatTimezoneLangInvalid("value=${date full ABC}"),

        VariationLang("value=${double},${integer},${string},${boolean},${long}",
                "value2=${double},${integer},${string},${boolean},${long}"),
        VariationLangSingle("value=${double},${integer},${string},${boolean},${long}");

        private final String[] eng;

        TempLang(String... eng) {
            this.eng = eng;
        }

        @Override
        public String[] getEngDefault() {
            return eng;
        }
    }

    @Test
    public void parseFirst() {
        moduleList.add(new DecimalFormatModule("###,###,###.0#"));
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        ICommandSender mockSender = mock(ICommandSender.class);
        when(mockSender.getLocale()).thenReturn(Locale.KOREAN);

        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.VariationLang, (sen, langman) -> {
            langman.addDouble(1324.5);
            langman.addString("ho");
        });

        assertEquals("value=1,324.5,null,ho,null,null", parsed);


        parsed = managerLanguage.parseFirst(TempLang.VariationLang);
        assertEquals("value=null,null,null,null,null", parsed);

        parsed = managerLanguage.parseFirst(TempLang.VariationLang, (sen, langman) -> {
            langman.addBoolean(true);
        });
        assertEquals("value=null,null,null,&atrue&f,null", parsed);


        parsed = managerLanguage.parseFirst(mockSender, TempLang.VariationLang);
        assertEquals("value=null,null,null,null,null", parsed);

        parsed = managerLanguage.parseFirst(TempLang.VariationLang, (sen, langman) -> {
            langman.addLong(888);
            langman.addDouble(1445246.389);
        });
        assertEquals("value=1,445,246.39,null,null,null,888", parsed);
    }

    @Test
    public void sendMessage() {
        moduleList.add(new DecimalFormatModule("###,###,###.0#"));
        ManagerLanguage managerLanguage = Guice.createInjector(moduleList)
                .getInstance(ManagerLanguage.class);

        ICommandSender mockSender = mock(ICommandSender.class);

        managerLanguage.sendMessage(mockSender, TempLang.VariationLangSingle);
        verify(mockSender).sendMessageRaw(anyBoolean(), eq("value=null,null,null,null,null"));

        managerLanguage.sendMessage(mockSender, TempLang.VariationLangSingle, (sen, langman) -> {
            langman.addLong(888);
            langman.addDouble(1445246.389);
        });
        verify(mockSender).sendMessageRaw(anyBoolean(), eq("value=1,445,246.39,null,null,null,888"));
    }
}