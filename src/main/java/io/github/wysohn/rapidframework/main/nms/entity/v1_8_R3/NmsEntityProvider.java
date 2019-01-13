/*******************************************************************************
 *     Copyright (C) 2017 wysohn
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package io.github.wysohn.rapidframework.main.nms.entity.v1_8_R3;

import com.mojang.authlib.GameProfile;
import io.github.wysohn.rapidframework.main.nms.entity.INmsEntityManager;
import io.github.wysohn.rapidframework.utils.reflections.ReflectionHelper;
import io.github.wysohn.rapidframework.utils.reflections.ReflectionUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NmsEntityProvider implements INmsEntityManager {
    @Override
    public String getLocale(Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        try {
            return ((String) ReflectionUtil.getField(cp.getHandle(), "locale")).split("_")[0];
        } catch (NoSuchFieldException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Player createFakePlayer(UUID uuid) {
        OfflinePlayer offp = Bukkit.getOfflinePlayer(uuid);
        if (offp == null || !offp.hasPlayedBefore())
            return null;

        MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer ws = ms.getWorldServer(0);
        GameProfile profile = new GameProfile(uuid, offp.getName());
        PlayerInteractManager pim = new PlayerInteractManager(ws);
        EntityPlayer ep = new EntityPlayer(ms, ws, profile, pim);

        Player player = ep.getBukkitEntity();
        player.loadData();
        return player;
    }

    @Override
    public void changeOfflinePlayerName(UUID uuid, String name) {
        MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer ws = ms.getWorldServer(0);
        GameProfile profile = new GameProfile(uuid, name);
        PlayerInteractManager pim = new PlayerInteractManager(ws);
        EntityPlayer ep = new EntityPlayer(ms, ws, profile, pim);

        ep.getBukkitEntity().saveData();
    }

    @Override
    public void destroyEntity(Player[] player, int[] entityID) {
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityID);

        for (Player p : player) {
            CraftPlayer cp = (CraftPlayer) p;
            EntityPlayer ep = cp.getHandle();
            ep.playerConnection.sendPacket(ppoed);
        }
    }

    @Override
    public void sendTeamColor(Player[] player, String teamName, String prefix, Set<String> playersUUID, int mode) {
        PacketPlayOutScoreboardTeam ppost = new PacketPlayOutScoreboardTeam();
        ReflectionHelper.setPrivateField(ppost, "a", teamName);
        ReflectionHelper.setPrivateField(ppost, "c", prefix);
        ReflectionHelper.setPrivateField(ppost, "h", playersUUID);
        ReflectionHelper.setPrivateField(ppost, "i", mode);

        for (Player p : player) {
            CraftPlayer cp = (CraftPlayer) p;
            EntityPlayer ep = cp.getHandle();
            ep.playerConnection.sendPacket(ppost);
        }
    }

    @Override
    public void swingRightArm(Player[] player) {
        List<PacketPlayOutAnimation> packets = new ArrayList<PacketPlayOutAnimation>();
        for (Player p : player) {
            CraftPlayer cp = (CraftPlayer) p;
            EntityPlayer ep = cp.getHandle();

            packets.add(new PacketPlayOutAnimation(ep, 3));
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            CraftPlayer cp = (CraftPlayer) p;
            EntityPlayer ep = cp.getHandle();
            for (Packet<?> packet : packets)
                ep.playerConnection.sendPacket(packet);
        }
    }

    @Override
    public float getYaw(Entity entity) {
        CraftEntity cf = (CraftEntity) entity;
        return cf.getHandle().getHeadRotation();
    }
}
