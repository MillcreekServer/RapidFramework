package io.github.wysohn.rapidframework3.bukkit.inject.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.wysohn.rapidframework3.bukkit.data.BukkitPlayer;
import io.github.wysohn.rapidframework3.bukkit.manager.api.ProtocolLibAPI;
import io.github.wysohn.rapidframework3.core.api.ManagerExternalAPI;
import io.github.wysohn.rapidframework3.core.message.Message;
import io.github.wysohn.rapidframework3.interfaces.ICommandSender;
import io.github.wysohn.rapidframework3.interfaces.message.IMessageSender;
import io.github.wysohn.rapidframework3.interfaces.message.IQueuedMessageConsumer;
import io.github.wysohn.rapidframework3.utils.Validation;

import java.util.Optional;

public class BukkitMessageSenderModule extends AbstractModule {
    @Provides
    @Singleton
    IMessageSender getMessageSender(ManagerExternalAPI api,
                                    IQueuedMessageConsumer queuedMessageConsumer) {
        return new IMessageSender() {
            private boolean failure = false;

            @Override
            public boolean isJsonEnabled() {
                return api.getAPI(ProtocolLibAPI.class).isPresent();
            }

            @Override
            public void enqueueMessage(ICommandSender sender, String[] parsed) {
                queuedMessageConsumer.accept(sender, parsed);
            }

            @Override
            public void send(ICommandSender sender, Message[] message, boolean conversation) {
                Validation.assertNotNull(sender);
                if (message == null)
                    return;

                Optional<ProtocolLibAPI> optApi = api.getAPI(ProtocolLibAPI.class);
                if (failure || !optApi.isPresent()) {
                    IMessageSender.super.send(sender, message, conversation);
                    return;
                }

                boolean sent = false;
                try {
                    // do not send if sender is engaged in conversation yet conversation is not set
                    if (sender.isConversing() && !conversation) {
                        return;
                    }

                    if (sender instanceof BukkitPlayer) {
                        optApi.get().send((BukkitPlayer) sender, message);
                        sent = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    failure = true;
                } finally {
                    // fallback just in case something went wrong so
                    // the chat continues with auxiliary sender
                    if (!sent) {
                        IMessageSender.super.send(sender, message, conversation);
                    }
                }
            }
        };
    }
}
