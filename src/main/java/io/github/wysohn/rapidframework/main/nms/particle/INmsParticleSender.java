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
package io.github.wysohn.rapidframework.main.nms.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface INmsParticleSender {
    public void sendPlayerOutParticle(Player[] player, int id, boolean distance, double x, double y, double z, int red,
	    int green, int blue, int speed, int count);

    public void sendPlayerOutParticle(Player[] player, int id, boolean distance, Location loc, int red, int green,
	    int blue, int speed, int count);

    public void showGlowingBlock(Player[] player, int entityID, UUID uuid, int x, int y, int z);
}
