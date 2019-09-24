package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ManagerLanguageTest {
    private enum TempLang implements Lang {
        SomeLang("This is message."),
        DecimalLang("Number is ${double}.");

        private String[] eng;

        TempLang(String... eng) {
            this.eng = eng;
        }

        @Override
        public String[] getEngDefault() {
            return eng;
        }
    }

    private ManagerLanguage managerLanguage;
    private KeyValueStorage mockStorage;

    @Before
    public void init() {
        managerLanguage = new ManagerLanguage(0);
        mockStorage = Mockito.mock(KeyValueStorage.class);

        Arrays.stream(TempLang.values())
                .forEach(managerLanguage::registerLanguage);

        managerLanguage.addLanguageStorage(Locale.KOREAN, mockStorage);
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
    public void addLanguageStorage() {
        //just test if the mockStorage is there
        Map<Locale, KeyValueStorage> map = (Map<Locale, KeyValueStorage>) Whitebox.getInternalState(managerLanguage, "languageSessions");
        KeyValueStorage storage = map.get(Locale.KOREAN);

        assertNotNull(storage);
        assertEquals(mockStorage, storage);
    }

    @Test
    public void setDecimalFormat() {
        double value = 12131451.67754;

        managerLanguage.setDecimalFormat(new DecimalFormat("##.0"));
        String parse = managerLanguage.parseFirst()
        assertEquals("The number is 12131451.68", );

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
    public void addDouble() {
    }

    @Test
    public void addInteger() {
    }

    @Test
    public void addString() {
    }

    @Test
    public void testAddString() {
    }

    @Test
    public void addBoolean() {
    }

    @Test
    public void addLong() {
    }

    @Test
    public void registerLanguage() {
    }

    @Test
    public void parse() {
    }

    @Test
    public void testParse() {
    }

    @Test
    public void testParse1() {
    }

    @Test
    public void parseFirst() {
    }

    @Test
    public void testParseFirst() {
    }

    @Test
    public void testParseFirst1() {
    }

    @Test
    public void sendMessage() {
    }

    @Test
    public void testSendMessage() {
    }
}