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
package io.github.wysohn.rapidframework.database.tasks;

import io.github.wysohn.rapidframework.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatabaseTransferTask implements Runnable {
    private Plugin plugin;
    private List<TransferPair> pairs;

    public DatabaseTransferTask(Plugin plugin, List<TransferPair> pairs) {
	super();
	this.plugin = plugin;
	this.pairs = pairs;
    }

    public DatabaseTransferTask(Plugin plugin, Set<TransferPair> pairs) {
	this.plugin = plugin;
	this.pairs = new ArrayList<TransferPair>();
	this.pairs.addAll(pairs);
    }

    @Override
    public void run() {
	Bukkit.getPluginManager().disablePlugin(plugin);

	int pairi = 0;
	for (TransferPair pair : pairs) {
	    Database from = pair.from;
	    Database to = pair.to;
	    Set<String> keys = from.getKeys();
	    int i = 0, percentage = -1;
	    for (String key : keys) {
		try {
		    Object data = from.load(key, null);
		    to.save(key, data);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    if (getPercentage(i, keys.size()) % 5 == 0) {
			percentage = getPercentage(i, keys.size());
			plugin.getLogger().info(pairi + ". " + pair.toString());
			plugin.getLogger().info("Transfer [" + percentage + "%] done...");
		    }
		    i++;
		}
	    }
	    plugin.getLogger().info(pairi + ". " + pair.toString());
	    plugin.getLogger().info("Transfer [100%] finished!");
	    pairi++;
	}

	Bukkit.getPluginManager().enablePlugin(plugin);
	System.gc();
    }

    private int getPercentage(int cur, int outOf) {
	return (int) (((double) cur / outOf) * 100);
    }

    public static class TransferPair<T> {
	private Database<T> from;
	private Database<T> to;

	public TransferPair(Database<T> from, Database<T> to) {
	    this.from = from;
	    this.to = to;
	}

	@Override
	public String toString() {
	    return from.getClass().getSimpleName() + " ==> " + to.getClass().getSimpleName();
	}

    }

}
