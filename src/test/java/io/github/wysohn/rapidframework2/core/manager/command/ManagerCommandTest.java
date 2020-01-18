package io.github.wysohn.rapidframework2.core.manager.command;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.lang.ManagerLanguage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

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

        SubCommand spyCommand = Mockito.spy(new SubCommand.Builder(mockMain, "somecmd")

                        .create());
        managerCommand.addCommand(spyCommand);
        managerCommand.enable();
    }

    @Test
    public void onCommand_notFound() throws Exception {
        Assert.assertTrue(managerCommand.onCommand(mockSender, "nottest", "nottest", new String[]{
                "nottest"
        }));
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