package io.github.wysohn.rapidframework3.core.chat;

import com.google.inject.*;
import io.github.wysohn.rapidframework3.core.inject.annotations.PluginDirectory;
import io.github.wysohn.rapidframework3.core.inject.factory.IStorageFactory;
import io.github.wysohn.rapidframework3.core.inject.module.LanguagesModule;
import io.github.wysohn.rapidframework3.core.language.ManagerLanguage;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.chat.IPlaceholderSupport;
import io.github.wysohn.rapidframework3.interfaces.store.IKeyValueStorage;
import io.github.wysohn.rapidframework3.testmodules.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AbstractChatManagerTest {
    private IKeyValueStorage mockStorage;

    private List<Module> moduleList = new LinkedList<>();

    @Before
    public void init() {
        mockStorage = mock(IKeyValueStorage.class);

        moduleList.add(new LanguagesModule());
        moduleList.add(new MockMainModule());
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(new MockPlaceholderModule());
        moduleList.add(new MockStorageFactoryModule(mockStorage));
        moduleList.add(new MockBroadcasterModule());
        moduleList.add(new MockMessageSenderModule());
        moduleList.add(new MockLoggerModule());
    }

    @Test
    public void onChat() {
        Injector injector = Guice.createInjector(moduleList);
        TempChatManager chatManager = injector.getInstance(TempChatManager.class);

        ICommandSender mockSender = mock(ICommandSender.class);
        Collection<? extends ICommandSender> mockRecipients = mock(Collection.class);

        Object section = new Object();
        when(mockStorage.get(anyString())).thenReturn(Optional.of(section));
        doReturn(true).when(mockStorage).isSection(eq(section));

        doAnswer(invocation -> {
            Consumer consumer = (Consumer) invocation.getArguments()[0];
            return null;
        }).when(mockRecipients).forEach(any(Consumer.class));

        when(mockStorage.get(eq(section), eq("value")))
                .thenReturn(Optional.of("This is value"));
        when(mockStorage.get(eq(section), eq("click_OpenUrl")))
                .thenReturn(Optional.of("This is click open url"));
        when(mockStorage.get(eq(section), eq("click_OpenFile")))
                .thenReturn(Optional.of("This is click open file"));
        when(mockStorage.get(eq(section), eq("click_RunCommand")))
                .thenReturn(Optional.of("This is click run command"));
        when(mockStorage.get(eq(section), eq("click_SuggestCommand")))
                .thenReturn(Optional.of("This is click suggest command"));
        when(mockStorage.get(eq(section), eq("hover_ShowText")))
                .thenReturn(Optional.of("This is hover text"));
        when(mockStorage.get(eq(section), eq("hover_ShowAchievement")))
                .thenReturn(Optional.of("This is hover achievement"));
        when(mockStorage.get(eq(section), eq("hover_ShowItem")))
                .thenReturn(Optional.of("This is hover item"));

        chatManager.onChat(mockSender,
                mockRecipients, "test");
    }

    @Singleton
    public static class TempChatManager extends AbstractChatManager {
        @Inject
        public TempChatManager(ManagerLanguage lang,
                               @PluginDirectory File pluginDir,
                               IStorageFactory storageFactory,
                               IPlaceholderSupport placeholderSupport) {
            super(lang, pluginDir, storageFactory, placeholderSupport);
        }

        @Override
        public void onChat(ICommandSender sender, Collection<? extends ICommandSender> recipients, String message) {
            super.onChat(sender, recipients, message);
        }
    }
}