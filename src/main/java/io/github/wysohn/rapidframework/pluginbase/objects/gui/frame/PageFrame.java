package io.github.wysohn.rapidframework.pluginbase.objects.gui.frame;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Button;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.Frame;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameCloseEventHandler;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameOpenEventHandler;

public abstract class PageFrame extends Frame {
    protected final String name;

    private PageNodeFrame head = null;
    private PageNodeFrame tail = null;
    private int size = 0;

    public PageFrame(PluginBase base, String name) {
	super(base, "meh", ChestSize.ONE);
	this.name = name;
    }

    protected PageNodeFrame add() {
	PageNodeFrame frame;
	if (head == null) {
	    frame = new PageNodeFrame(base, this, name, 1);
	    head = frame;
	    tail = frame;
	} else {
	    frame = new PageNodeFrame(base, this, name, tail.getIndex() + 1);
	    frame.setPrevious(tail);
	    tail.setNext(frame);
	    tail = frame;
	}
	size++;
	return frame;
    }

    protected boolean remove(PageNodeFrame frame) {
	return remove(frame, head);
    }

    private boolean remove(PageNodeFrame target, PageNodeFrame cur) {
	if (cur == null) {
	    return false;
	} else if (target.equals(cur)) {
	    if (cur.getPrevious() != null)
		cur.getPrevious().setNext(cur.getNext());
	    if (cur.getNext() != null)
		cur.getNext().setPrevious(cur.getPrevious());
	    cur.setNext(null);
	    cur.setPrevious(null);
	    System.gc();
	    size--;
	    return true;
	} else {
	    return remove(target, cur.getNext());
	}
    }

    public PageNodeFrame getHead() {
	return head;
    }

    public PageNodeFrame getTail() {
	return tail;
    }

    public int getPageSize() {
	return size;
    }

    @Override
    public String getName() {
	return name;
    }

    @Deprecated
    @Override
    public Inventory getInstance() {
	throw new RuntimeException("This method is not supported.");
    }

    @Deprecated
    @Override
    public int addButton(Button btn) {
	throw new RuntimeException("This method is not supported.");
    }

    @Deprecated
    @Override
    public ChestSize getSize() {
	throw new RuntimeException("This method is not supported.");
    }

    @Deprecated
    @Override
    public void setButton(int index, Button btn) {
	throw new RuntimeException("This method is not supported.");
    }

    @Deprecated
    @Override
    public Button getButton(int index) {
	throw new RuntimeException("This method is not supported.");
    }

    protected FrameOpenEventHandler openHandler;
    protected FrameCloseEventHandler closeHandler;

    @Override
    public void setOpenEventHandler(FrameOpenEventHandler openEventHandler) {
	this.openHandler = openEventHandler;
    }

    @Override
    public void setCloseEventHandler(FrameCloseEventHandler closeEventHandler) {
	this.closeHandler = closeEventHandler;
    }

    @Override
    public void show(Player player) {
	if (openHandler != null)
	    openHandler.onOpen(player);

	getHead().show(player);
    }

    @Deprecated
    @Override
    public void clear() {
	throw new RuntimeException("This method is not supported.");
    }

    @Deprecated
    @Override
    public Inventory getInventory() {
	throw new RuntimeException("This method is not supported.");
    }
}
