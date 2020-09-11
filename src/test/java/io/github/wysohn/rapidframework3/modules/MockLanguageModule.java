package io.github.wysohn.rapidframework3.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.core.interfaces.language.ILangSessionFactory;
import io.github.wysohn.rapidframework3.core.interfaces.message.IBroadcaster;
import io.github.wysohn.rapidframework3.core.interfaces.message.IMessageSender;

import java.util.Locale;

public class MockLanguageModule extends AbstractModule {
    private final ILangSessionFactory langSessionFactory;
    private final IBroadcaster broadcaster;

    public MockLanguageModule(ILangSessionFactory langSessionFactory, IBroadcaster broadcaster) {
        this.langSessionFactory = langSessionFactory;
        this.broadcaster = broadcaster;
    }

    @Provides
    Locale getLocale() {
        return Locale.ENGLISH;
    }

    @Provides
    ILangSessionFactory getFactory() {
        return langSessionFactory;
    }

    @Provides
    IBroadcaster getBroadcaster() {
        return broadcaster;
    }

    @Provides
    IMessageSender getMessageSender() {
        return () -> false;
    }
}
