package io.github.wysohn.rapidframework.pluginbase.manager.item;

import java.util.UUID;

import org.bukkit.event.Listener;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;

public class ManagerCustomItem extends PluginManager<PluginBase> implements Listener {
    private static final UUID ITEM_NBT_KEY = UUID.fromString("13297c0b-cf84-4431-a782-8efc5ef714cb");

    public ManagerCustomItem(PluginBase base, int loadPriority) {
	super(base, loadPriority);
    }

    @Override
    protected void onEnable() throws Exception {

    }

    @Override
    protected void onDisable() throws Exception {

    }

    @Override
    protected void onReload() throws Exception {

    }

}
