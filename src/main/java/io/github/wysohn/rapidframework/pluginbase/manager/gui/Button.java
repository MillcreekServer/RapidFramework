package io.github.wysohn.rapidframework.pluginbase.manager.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.button.ButtonEventHandler;

public class Button {
    protected transient final PluginBase base;
    private transient Frame parent;
    private transient final ItemStack IS;

    private transient ButtonEventHandler leftClickEventHandler;
    private transient ButtonEventHandler rightClickEventHandler;
    private transient ButtonEventHandler shiftLeftClickEventHandler;
    private transient ButtonEventHandler shiftRightClickEventHandler;

    protected Button(PluginBase base, Frame parent, ItemStack IS) {
	this.base = base;
	this.parent = parent;
	this.IS = IS;
    }

    public void setParent(Frame parent) {
	this.parent = parent;
    }

    public Frame getParent() {
	return parent;
    }

    public ItemStack getIS() {
	return IS;
    }

    public Button updateDisplayName(String displayName) {
	ItemMeta IM = IS.getItemMeta();
	IM.setDisplayName(displayName);
	IS.setItemMeta(IM);
	return this;
    }

    public Button updateLore(List<String> lore) {
	ItemMeta IM = IS.getItemMeta();
	IM.setLore(lore);
	IS.setItemMeta(IM);
	return this;
    }

    public Button setLeftClickEventHandler(ButtonEventHandler leftClickEventHandler) {
	this.leftClickEventHandler = leftClickEventHandler;
	return this;
    }

    public Button setRightClickEventHandler(ButtonEventHandler rightClickEventHandler) {
	this.rightClickEventHandler = rightClickEventHandler;
	return this;
    }

    public Button setShiftLeftClickEventHandler(ButtonEventHandler shiftLeftClickEventHandler) {
	this.shiftLeftClickEventHandler = shiftLeftClickEventHandler;
	return this;
    }

    public Button setShiftRightClickEventHandler(ButtonEventHandler shiftRightClickEventHandler) {
	this.shiftRightClickEventHandler = shiftRightClickEventHandler;
	return this;
    }

    public void handleEvent(InventoryClickEvent e) {
	e.setCancelled(true);

	if (!(e.getWhoClicked() instanceof Player))
	    return;

	if (e.isShiftClick()) {
	    if (e.isRightClick()) {
		if (shiftRightClickEventHandler != null)
		    shiftRightClickEventHandler.onClick((Player) e.getWhoClicked());
	    } else if (e.isLeftClick()) {
		if (shiftLeftClickEventHandler != null)
		    shiftLeftClickEventHandler.onClick((Player) e.getWhoClicked());
	    }
	} else {
	    if (e.isRightClick()) {
		if (rightClickEventHandler != null)
		    rightClickEventHandler.onClick((Player) e.getWhoClicked());
	    } else if (e.isLeftClick()) {
		if (leftClickEventHandler != null)
		    leftClickEventHandler.onClick((Player) e.getWhoClicked());
	    }
	}
    }

    public static int getIndex(int row, int col) {
	return (row * 9) + col;
    }

    public static class Builder {
	private PluginBase base;
	private Button button;

	private Builder(PluginBase base, Frame parent, ItemStack IS) {
	    button = new Button(base, parent, IS);
	}

	public Builder withDisplayName(String displayName) {
	    button.updateDisplayName(displayName);
	    return this;
	}

	public Builder addLore(String lore) {
	    ItemMeta IM = button.getIS().getItemMeta();

	    List<String> list = IM.getLore();
	    if (list == null)
		list = new ArrayList<>();
	    list.add(lore);

	    IM.setLore(list);

	    button.IS.setItemMeta(IM);

	    return this;
	}

	public Builder onClickReact(ButtonEventHandler handler) {
	    button.leftClickEventHandler = handler;
	    button.rightClickEventHandler = handler;
	    button.shiftLeftClickEventHandler = handler;
	    button.shiftRightClickEventHandler = handler;
	    return this;
	}

	public Builder onClickLeftReact(ButtonEventHandler handler) {
	    button.leftClickEventHandler = handler;
	    button.shiftLeftClickEventHandler = handler;
	    return this;
	}

	public Builder onClickRightReact(ButtonEventHandler handler) {
	    button.rightClickEventHandler = handler;
	    button.shiftRightClickEventHandler = handler;
	    return this;
	}

	public Builder onClickLeftOnlyReact(ButtonEventHandler handler) {
	    button.leftClickEventHandler = handler;
	    return this;
	}

	public Builder onClickLeftShittReact(ButtonEventHandler handler) {
	    button.shiftLeftClickEventHandler = handler;
	    return this;
	}

	public Builder onClickRightOnlyReact(ButtonEventHandler handler) {
	    button.rightClickEventHandler = handler;
	    return this;
	}

	public Builder onClickRightShiftReact(ButtonEventHandler handler) {
	    button.shiftRightClickEventHandler = handler;
	    return this;
	}

	public Button build() {
	    return button;
	}
    }
}
