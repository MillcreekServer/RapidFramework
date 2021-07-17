package io.github.wysohn.rapidframework4.bukkit.data;

import io.github.wysohn.rapidframework4.interfaces.ICommandSender;
import io.github.wysohn.rapidframework4.interfaces.IPluginObject;
import io.github.wysohn.rapidframework4.interfaces.block.IBlock;
import io.github.wysohn.rapidframework4.interfaces.entity.IPlayer;
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
