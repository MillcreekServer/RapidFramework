package io.github.wysohn.rapidframework2.bukkit.main.objects;

import io.github.wysohn.rapidframework2.core.interfaces.IPluginObject;
import io.github.wysohn.rapidframework2.core.interfaces.block.IBlock;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class BukkitWrapper {
    public static IBlock block(Block block) {
        return new BukkitBlock(block);
    }

    public static IPlayer player(Player player) {
        return new BukkitPlayer(player.getUniqueId()).setSender(player);
    }

    public static ICommandSender sender(CommandSender sender) {
        if (sender instanceof Player) {
            return player((Player) sender);
        } else {
            return new BukkitCommandSender().setSender(sender);
        }
    }

    public static IPluginObject entity(Entity entity) {
        return new BukkitEntity(entity);
    }
}
