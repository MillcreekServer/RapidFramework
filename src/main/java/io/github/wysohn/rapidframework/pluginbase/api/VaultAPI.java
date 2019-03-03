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
package io.github.wysohn.rapidframework.pluginbase.api;

import io.github.wysohn.rapidframework.pluginbase.PluginAPISupport.APISupport;
import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAPI extends APISupport {
    public Permission permission = null;
    public Economy economy = null;
    public Chat chat = null;

    public VaultAPI(PluginBase base) {
	super(base);
    }

    @Override
    public boolean init() throws Exception {
	if (setupPermissions()) {
	    base.getLogger().info("Vault permission hooked.");
	}
	if (setupChat()) {
	    base.getLogger().info("Vault chat hooked.");
	}
	if (setupEconomy()) {
	    base.getLogger().info("Vault economy hooked.");
	}
	return true;
    }

    private boolean setupPermissions() {
	RegisteredServiceProvider<Permission> permissionProvider = base.getServer().getServicesManager()
		.getRegistration(net.milkbowl.vault.permission.Permission.class);
	if (permissionProvider != null) {
	    permission = permissionProvider.getProvider();
	}
	return (permission != null);
    }

    private boolean setupChat() {
	RegisteredServiceProvider<Chat> chatProvider = base.getServer().getServicesManager()
		.getRegistration(net.milkbowl.vault.chat.Chat.class);
	if (chatProvider != null) {
	    chat = chatProvider.getProvider();
	}

	return (chat != null);
    }

    private boolean setupEconomy() {
	RegisteredServiceProvider<Economy> economyProvider = base.getServer().getServicesManager()
		.getRegistration(net.milkbowl.vault.economy.Economy.class);
	if (economyProvider != null) {
	    economy = economyProvider.getProvider();
	}

	return (economy != null);
    }

    public boolean isPermissionEnabled() {
	return permission != null;
    }

    public boolean isEconomyEnabled() {
	return economy != null;
    }

    public boolean isChatEnabled() {
	return chat != null;
    }
}
