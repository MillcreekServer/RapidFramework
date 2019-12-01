package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.KeyValueStorage;
import io.github.wysohn.rapidframework2.core.main.PluginMain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ManagerLanguageTest{
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
    	PluginMain mockMain = Mockito.mock(PluginMain.class);
    	Logger mockLogger = Mockito.mock(Logger.class);
    	Mockito.when(mockMain.getLogger()).thenReturn(mockLogger);
    	
        managerLanguage = new ManagerLanguage(0);
        mockStorage = Mockito.mock(KeyValueStorage.class);

        Arrays.stream(TempLang.values())
                .forEach(managerLanguage::registerLanguage);

        managerLanguage.addLanguageStorage(Locale.KOREAN, mockStorage);
        
        Whitebox.setInternalState(managerLanguage, "main", mockMain);
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
        String parse = managerLanguage.parseFirst(Locale.KOREAN,
                TempLang.DecimalLang, (l -> {
                    l.addDouble(value);
                }));
        assertEquals("The number is 12131451.68", parse);

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
    public void parseFirst() {

    }

    @Test
    public void sendMessage() {

    }
}