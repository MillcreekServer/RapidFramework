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
package io.github.wysohn.rapidframework.main.nms.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface INmsEntityManager {
    String getLocale(Player player);

    Player createFakePlayer(final UUID uuid);

    void changeOfflinePlayerName(UUID uuid, String name);

    void destroyEntity(Player[] player, int[] entityID);

    void sendTeamColor(Player player[], String teamName, String prefix, Set<String> playersUUID, int mode);

    void swingRightArm(Player[] player);

    float getYaw(Entity entity);
}
