package io.github.wysohn.rapidframework3.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.message.IMessageSender;

public class MockMessageSenderModule extends AbstractModule {
    @Provides
    IMessageSender getSender() {
        return new IMessageSender() {
            @Override
            public boolean isJsonEnabled() {
                return false;
            }

            @Override
            public void enqueueMessage(ICommandSender sender, String[] parsed) {

            }
        };
    }
}
