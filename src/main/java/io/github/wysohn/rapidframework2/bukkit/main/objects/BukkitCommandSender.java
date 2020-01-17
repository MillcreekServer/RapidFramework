package io.github.wysohn.rapidframework2.bukkit.main.objects;

import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class BukkitCommandSender<Sender extends CommandSender> implements ICommandSender {
    protected transient Sender sender;

    public BukkitCommandSender() {
    }

    @Override
    public void sendMessageRaw(String... msg) {
        sender.sendMessage(msg);
    }

    @Override
    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public boolean hasPermission(String... permissions) {
        return Arrays.stream(permissions).anyMatch(sender::hasPermission);
    }

    @Override
    public String getDisplayName() {
        return Optional.ofNullable(sender)
                .map(CommandSender::getName)
                .orElse("<Unknown>");
    }

    @Override
    public UUID getUuid() {
        return null;
    }

    public BukkitCommandSender<Sender> setSender(Sender sender) {
        this.sender = sender;
        return this;
    }

    public Sender getSender() {
        return sender;
    }
}
