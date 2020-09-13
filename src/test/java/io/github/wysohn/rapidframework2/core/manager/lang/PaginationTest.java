package io.github.wysohn.rapidframework2.core.manager.lang;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.main.PluginMain;
import io.github.wysohn.rapidframework2.core.manager.common.message.MessageBuilder;
import io.github.wysohn.rapidframework2.core.manager.lang.page.ListWrapper;
import io.github.wysohn.rapidframework2.core.manager.lang.page.Pagination;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PaginationTest {
    PluginMain mockMain;
    ManagerLanguage mockLang;
    ICommandSender mockSender;

    @Before
    public void init() throws Exception {
        mockMain = Mockito.mock(PluginMain.class);
        mockLang = Mockito.mock(ManagerLanguage.class);
        mockSender = Mockito.mock(ICommandSender.class);

        Mockito.when(mockMain.lang()).thenReturn(mockLang);

    }

    @Test
    public void showWithJson() throws InterruptedException {
        Mockito.when(mockLang.isJsonEnabled()).thenReturn(true);

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add("message" + i);
        }

        String cmd = "/some command";
        Pagination<String> pagination = new Pagination<>(mockMain, ListWrapper.wrap(messages), 6,
                "title", cmd);

        pagination.show(mockSender, 0, (sender, message, i) -> MessageBuilder.forMessage(message).build());
        pagination.shutdown();

        // pad
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.empty()));
        // contents
        for (int i = 0; i < 6; i++)
            verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage(messages.get(i)).build()));
        // buttons
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage("")
                .append(Pagination.LEFT_ARROW)
                .withHoverShowText(cmd + " 0")
                .withClickRunCommand(cmd + " 0")
                .append(Pagination.HOME)
                .withHoverShowText(cmd)
                .withClickRunCommand(cmd)
                .append(Pagination.RIGHT_ARROW)
                .withHoverShowText(cmd + " 2")
                .withClickRunCommand(cmd + " 2")
                .build()));
    }

    @Test
    public void showWithJson2() throws InterruptedException {
        Mockito.when(mockLang.isJsonEnabled()).thenReturn(true);

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add("message" + i);
        }

        String cmd = "/some command";
        Pagination<String> pagination = new Pagination<>(mockMain, ListWrapper.wrap(messages), 6,
                "title", cmd);

        pagination.show(mockSender, 1, (sender, message, i) -> MessageBuilder.forMessage(message).build());
        pagination.shutdown();

        // pad
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.empty()));
        // contents
        for (int i = 6; i < 10; i++)
            verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage(messages.get(i)).build()));
        // buttons
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage("")
                .append(Pagination.LEFT_ARROW)
                .withHoverShowText(cmd + " 1")
                .withClickRunCommand(cmd + " 1")
                .append(Pagination.HOME)
                .withHoverShowText(cmd)
                .withClickRunCommand(cmd)
                .append(Pagination.RIGHT_ARROW)
                .withHoverShowText(cmd + " 3")
                .withClickRunCommand(cmd + " 3")
                .build()));
    }

    @Test
    public void showWithJson3() throws InterruptedException {
        Mockito.when(mockLang.isJsonEnabled()).thenReturn(true);

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add("message" + i);
        }

        String cmd = "/some command";
        Pagination<String> pagination = new Pagination<>(mockMain, ListWrapper.wrap(messages), 6,
                "title", cmd);

        pagination.show(mockSender, 3, (sender, message, i) -> MessageBuilder.forMessage(message).build());
        pagination.shutdown();

        // pad
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.empty()));
        // contents
        for (int i = 6; i < 10; i++)
            verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage(messages.get(i)).build()));
        // buttons
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage("")
                .append(Pagination.LEFT_ARROW)
                .withHoverShowText(cmd + " 1")
                .withClickRunCommand(cmd + " 1")
                .append(Pagination.HOME)
                .withHoverShowText(cmd)
                .withClickRunCommand(cmd)
                .append(Pagination.RIGHT_ARROW)
                .withHoverShowText(cmd + " 3")
                .withClickRunCommand(cmd + " 3")
                .build()));
    }

    @Test
    public void showWithJson4() throws InterruptedException {
        Mockito.when(mockLang.isJsonEnabled()).thenReturn(true);

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add("message" + i);
        }

        String cmd = "/some command";
        Pagination<String> pagination = new Pagination<>(mockMain, ListWrapper.wrap(messages), 6,
                "title", cmd);

        pagination.show(mockSender, 0, (sender, message, i) -> MessageBuilder.forMessage(message).build());
        pagination.shutdown();

        // pad
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.empty()));
        // contents
        for (int i = 0; i < 6; i++)
            verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage(messages.get(i)).build()));
        // buttons
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage("")
                .append(Pagination.LEFT_ARROW)
                .withHoverShowText(cmd + " 0")
                .withClickRunCommand(cmd + " 0")
                .append(Pagination.HOME)
                .withHoverShowText(cmd)
                .withClickRunCommand(cmd)
                .append(Pagination.RIGHT_ARROW)
                .withHoverShowText(cmd + " 2")
                .withClickRunCommand(cmd + " 2")
                .build()));
    }

    @Test
    public void showWithJson5() throws InterruptedException {
        Mockito.when(mockLang.isJsonEnabled()).thenReturn(true);

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            messages.add("message" + i);
        }

        String cmd = "/some command";
        Pagination<String> pagination = new Pagination<>(mockMain, ListWrapper.wrap(messages), 10,
                "title", cmd);

        pagination.show(mockSender, 8, (sender, message, i) -> MessageBuilder.forMessage(message).build());
        pagination.shutdown();

        // pad
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.empty()));
        // contents
        for (int i = 10; i < 12; i++)
            verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage(messages.get(i)).build()));
        // buttons
        verify(mockLang).sendRawMessage(eq(mockSender), eq(MessageBuilder.forMessage("")
                .append(Pagination.LEFT_ARROW)
                .withHoverShowText(cmd + " 1")
                .withClickRunCommand(cmd + " 1")
                .append(Pagination.HOME)
                .withHoverShowText(cmd)
                .withClickRunCommand(cmd)
                .append(Pagination.RIGHT_ARROW)
                .withHoverShowText(cmd + " 3")
                .withClickRunCommand(cmd + " 3")
                .build()));
    }

    @Test
    public void showWithoutJson() throws InterruptedException {
        Mockito.when(mockLang.isJsonEnabled()).thenReturn(false);

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            messages.add("message" + i);
        }

        String cmd = "/some command";
        Pagination<String> pagination = new Pagination<>(mockMain, ListWrapper.wrap(messages), 6,
                "title", cmd);

        pagination.show(mockSender, 0, (sender, message, i) -> MessageBuilder.forMessage(message).build());
        pagination.shutdown();

        // three times
        verify(mockLang, times(3)).sendMessage(eq(mockSender), any(), any());
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