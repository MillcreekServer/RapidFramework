package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class ManagerLanguageTest {
    private final Date date = new Date(1592466358000L); // Thursday, June 18, 2020 7:45:58 AM GMT

    @Before
    public void init() {
        PluginMain mockMain = Mockito.mock(PluginMain.class);
        Logger mockLogger = Mockito.mock(Logger.class);
        Mockito.when(mockMain.getLogger()).thenReturn(mockLogger);

        managerLanguage = new ManagerLanguage(0, locale -> {
            if (locale == Locale.ENGLISH)
                return new LanguageSession(mockStorageDef);
            else if (locale == Locale.KOREAN)
                return new LanguageSession(mockStorage);
            else
                return null;
        });
        mockStorageDef = Mockito.mock(KeyValueStorage.class);
        mockStorage = Mockito.mock(KeyValueStorage.class);

        Arrays.stream(TempLang.values())
                .forEach(managerLanguage::registerLanguage);

        Whitebox.setInternalState(managerLanguage, "main", mockMain);
    }

    private ManagerLanguage managerLanguage;
    private KeyValueStorage mockStorageDef;
    private KeyValueStorage mockStorage;

    @Test
    public void addDouble() {
        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DoubleLang, (sen, langman) -> {
            langman.addDouble(1023.2);
        });
        Assert.assertEquals("value=1,023.2,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DoubleLang, (sen, langman) -> {
            langman.addDouble(5.2);
            langman.addDouble(993223.12);
        });
        Assert.assertEquals("value=5.2,993,223.12", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DoubleLang, (sen, langman) -> {
        });
        Assert.assertEquals("value=null,null", parsed);
    }

    @Test
    public void getDefaultLang() {
        assertEquals(Locale.ENGLISH, managerLanguage.getDefaultLang());
    }

    @Test
    public void setDefaultLang() {
        managerLanguage.setDefaultLang(Locale.KOREAN);
        assertEquals(Locale.KOREAN, managerLanguage.getDefaultLang());
    }

    @Test
    public void setDecimalFormat() {
        double value = 12131451.67754;

        managerLanguage.setDecimalFormat(new DecimalFormat("##.0"));
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
        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.IntegerLang, (sen, langman) -> {
            langman.addInteger(1452);
        });
        Assert.assertEquals("value=1452,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.IntegerLang, (sen, langman) -> {
            langman.addInteger(6231);
            langman.addInteger(32905939);
        });
        Assert.assertEquals("value=6231,32905939", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.IntegerLang, (sen, langman) -> {
        });
        Assert.assertEquals("value=null,null", parsed);
    }

    @Test
    public void addString() {
        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.StringLang, (sen, langman) -> {
            langman.addString("test");
        });
        Assert.assertEquals("value=test,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.StringLang, (sen, langman) -> {
            langman.addString("este");
            langman.addString("tetetet");
        });
        Assert.assertEquals("value=este,tetetet", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.StringLang, (sen, langman) -> {
            langman.addString(new String[]{"hehehe", "rtrtrt"});
        });
        Assert.assertEquals("value=hehehe,rtrtrt", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.StringLang, (sen, langman) -> {
        });
        Assert.assertEquals("value=null,null", parsed);
    }

    @Test
    public void addBoolean() {
        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.BooleanLang, (sen, langman) -> {
            langman.addBoolean(true);
        });
        Assert.assertEquals("value=&atrue&f,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.BooleanLang, (sen, langman) -> {
            langman.addBoolean(false);
            langman.addBoolean(true);
        });
        Assert.assertEquals("value=&cfalse&f,&atrue&f", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.BooleanLang, (sen, langman) -> {
        });
        Assert.assertEquals("value=null,null", parsed);
    }

    @Test
    public void addLong() {
        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.LongLang, (sen, langman) -> {
            langman.addLong(1452);
        });
        Assert.assertEquals("value=1452,null", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.LongLang, (sen, langman) -> {
            langman.addLong(6231);
            langman.addLong(32905939);
        });
        Assert.assertEquals("value=6231,32905939", parsed);

        parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.LongLang, (sen, langman) -> {
        });
        Assert.assertEquals("value=null,null", parsed);
    }

    @Test
    public void addDate1() {
        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateLang, (sen, langman) -> {
            langman.addDate(date);
        });
        Assert.assertEquals("value=6/18/20 7:45 AM", parsed);

        String parsed2 = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DateLang, (sen, langman) -> {
            langman.addDate(date);
        });
        Assert.assertEquals("value=20. 6. 18 오전 7:45", parsed2);
    }

    @Test
    public void addDate2() {
        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateFormatLang, (sen, langman) -> {
            langman.addDate(date);
        });
        Assert.assertEquals("value=Thursday, June 18, 2020 7:45:58 AM UTC", parsed);

        String parsed2 = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DateFormatLang, (sen, langman) -> {
            langman.addDate(date);
        });
        Assert.assertEquals("value=2020년 6월 18일 목요일 오전 7시 45분 58초 UTC", parsed2);
    }

    @Test
    public void addDate2_1() {
        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateFormatLangInvalid, (sen, langman) -> {
            langman.addDate(date);
        });
        Assert.assertEquals("value=?illegal 'date' format?", parsed);
    }

    @Test
    public void addDate3() {
        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateFormatTimezoneLang, (sen, langman) -> {
            langman.addDate(date);
        });
        Assert.assertEquals("value=Thursday, June 18, 2020 4:45:58 PM GMT+09:00", parsed);

        String parsed2 = managerLanguage.parseFirst(Locale.KOREAN, TempLang.DateFormatTimezoneLang, (sen, langman) -> {
            langman.addDate(date);
        });
        Assert.assertEquals("value=2020년 6월 18일 목요일 오후 4시 45분 58초 GMT+09:00", parsed2);
    }

    @Test
    public void addDate3_1() {
        String parsed = managerLanguage.parseFirst(Locale.ENGLISH, TempLang.DateFormatTimezoneLangInvalid, (sen, langman) -> {
            langman.addDate(date);
        });
        // TimeZone.getTimeZone() returns GMT if invalid timezone is passed.
        Assert.assertEquals("value=Thursday, June 18, 2020 7:45:58 AM GMT", parsed);
    }

    @Test
    public void parse() {
        ICommandSender mockSender = Mockito.mock(ICommandSender.class);
        Mockito.when(mockSender.getLocale()).thenReturn(Locale.KOREAN);


        String[] parsed = managerLanguage.parse(Locale.KOREAN, null, TempLang.VariationLang, (sen, langman) -> {
            langman.addDouble(1324.5);
            langman.addString("ho");
        });

        Assert.assertEquals(2, parsed.length);
        Assert.assertEquals("value=1,324.5,null,ho,null,null", parsed[0]);
        Assert.assertEquals("value2=null,null,null,null,null", parsed[1]);


        parsed = managerLanguage.parse(TempLang.VariationLang);

        Assert.assertEquals(2, parsed.length);
        Assert.assertEquals("value=null,null,null,null,null", parsed[0]);
        Assert.assertEquals("value2=null,null,null,null,null", parsed[1]);

        parsed = managerLanguage.parse(TempLang.VariationLang, (sen, langman) -> {
            langman.addString(new String[]{"abc", "efg"});
        });

        Assert.assertEquals(2, parsed.length);
        Assert.assertEquals("value=null,null,abc,null,null", parsed[0]);
        Assert.assertEquals("value2=null,null,efg,null,null", parsed[1]);


        parsed = managerLanguage.parse(mockSender, TempLang.VariationLang);

        Assert.assertEquals(2, parsed.length);
        Assert.assertEquals("value=null,null,null,null,null", parsed[0]);
        Assert.assertEquals("value2=null,null,null,null,null", parsed[1]);

        parsed = managerLanguage.parse(mockSender, TempLang.VariationLang, (sen, langman) -> {
            langman.addInteger(555);
            langman.addBoolean(false);
            langman.addBoolean(true);
            langman.addLong(8987);
        });

        Assert.assertEquals(2, parsed.length);
        Assert.assertEquals("value=null,555,null,&cfalse&f,8987", parsed[0]);
        Assert.assertEquals("value2=null,null,null,&atrue&f,null", parsed[1]);
    }

    private enum TempLang implements Lang {
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
        ICommandSender mockSender = Mockito.mock(ICommandSender.class);
        Mockito.when(mockSender.getLocale()).thenReturn(Locale.KOREAN);


        String parsed = managerLanguage.parseFirst(Locale.KOREAN, TempLang.VariationLang, (sen, langman) -> {
            langman.addDouble(1324.5);
            langman.addString("ho");
        });

        Assert.assertEquals("value=1,324.5,null,ho,null,null", parsed);


        parsed = managerLanguage.parseFirst(TempLang.VariationLang);
        Assert.assertEquals("value=null,null,null,null,null", parsed);

        parsed = managerLanguage.parseFirst(TempLang.VariationLang, (sen, langman) -> {
            langman.addBoolean(true);
        });
        Assert.assertEquals("value=null,null,null,&atrue&f,null", parsed);


        parsed = managerLanguage.parseFirst(mockSender, TempLang.VariationLang);
        Assert.assertEquals("value=null,null,null,null,null", parsed);

        parsed = managerLanguage.parseFirst(TempLang.VariationLang, (sen, langman) -> {
            langman.addLong(888);
            langman.addDouble(1445246.389);
        });
        Assert.assertEquals("value=1,445,246.39,null,null,null,888", parsed);
    }

    @Test
    public void sendMessage() {
        ICommandSender mockSender = Mockito.mock(ICommandSender.class);

        managerLanguage.sendMessage(mockSender, TempLang.VariationLangSingle);
        Mockito.verify(mockSender).sendMessageRaw(Mockito.anyBoolean(), Mockito.eq("value=null,null,null,null,null"));

        managerLanguage.sendMessage(mockSender, TempLang.VariationLangSingle, (sen, langman) -> {
            langman.addLong(888);
            langman.addDouble(1445246.389);
        });
        Mockito.verify(mockSender).sendMessageRaw(Mockito.anyBoolean(), Mockito.eq("value=1,445,246.39,null,null,null,888"));
    }
}