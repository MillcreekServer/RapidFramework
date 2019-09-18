package io.github.wysohn.rapidframework.pluginbase.manager.gui;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.PluginManager;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.button.ButtonEventHandler;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameCloseEventHandler;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameOpenEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ManagerGUI extends PluginManager<PluginBase> implements Listener {
    private static final Map<InventoryWrapper, Frame> registeredFrames = new ConcurrentHashMap<InventoryWrapper, Frame>();

    public ManagerGUI(PluginBase base, int loadPriority) {
        super(base, loadPriority);
    }

    void registerFrame(Frame frame) {
        registeredFrames.put(new InventoryWrapper(frame.getInstance()), frame);

        if (base.getPluginConfig().Plugin_Debugging)
            base.getLogger().info("GUI [" + frame.getInstance().hashCode() + "] is registered!");
    }

    void unregisterFrame(Frame frame) {
        registeredFrames.remove(new InventoryWrapper(frame.getInstance()));

        if (base.getPluginConfig().Plugin_Debugging)
            base.getLogger().info("GUI [" + frame.getInstance().hashCode() + "] is unregistered!");
    }

    @Override
    protected void onEnable() throws Exception {

    }

    @Override
    protected void onReload() throws Exception {

    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        InventoryWrapper inv = new InventoryWrapper(e.getInventory());
        if (!registeredFrames.containsKey(inv))
            return;

        Frame frame = registeredFrames.get(inv);

        int rawSlot = e.getRawSlot();
        if (rawSlot >= 0 && rawSlot < frame.buttons.length) {
            Button button = frame.buttons[rawSlot];
            if (button != null)
                button.handleEvent((InventoryClickEvent) e);
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e) {
        InventoryWrapper inv = new InventoryWrapper(e.getInventory());
        if (!registeredFrames.containsKey(inv))
            return;

        Frame frame = registeredFrames.get(inv);

        if (!(e.getPlayer() instanceof Player))
            return;

        if (frame.closeEventHandler != null)
            frame.closeEventHandler.onClose((Player) e.getPlayer());

        unregisterFrame(frame);
    }

    private static class InventoryWrapper {
        Inventory inv;

        InventoryWrapper(Inventory inv) {
            this.inv = inv;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + inv.getName().hashCode();
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof InventoryWrapper))
                return false;
            InventoryWrapper other = (InventoryWrapper) obj;
            if (inv == null) {
                if (other.inv != null)
                    return false;
            } else if (!inv.equals(other.inv))
                return false;

            return true;
        }
    }


    /**
     * Frames register itself automatically as constructor is called
     *
     * @author wysohn
     */
    public static class Frame {
        protected transient final ManagerGUI managerGUI;

        transient final String name;
        transient final ChestSize size;

        transient final Inventory instance;

        transient Button[] buttons;

        transient FrameOpenEventHandler openEventHandler;
        transient FrameCloseEventHandler closeEventHandler;

        /**
         * @param name unique name for inventory; shouldn't be null
         * @param size size of inventory; shouldn't be null
         */
        protected Frame(ManagerGUI managerGUI, String name, ChestSize size) {
            this.managerGUI = managerGUI;
            this.name = name;
            this.size = size;

            buttons = new Button[size.size];

            instance = Bukkit.createInventory(null, size.size, name);
        }

        public String getName() {
            return name;
        }

        public Inventory getInstance() {
            return instance;
        }

        /**
         * @param btn
         * @return magic value
         * @deprecated use setButton()
         */
        public int addButton(Button btn) {
            return -1;
        }

        public ChestSize getSize() {
            return size;
        }

        public void setButton(int index, Button btn) {
            buttons[index] = btn;
        }

        public Button getButton(int index) {
            return buttons[index];
        }

        public void setOpenEventHandler(FrameOpenEventHandler openEventHandler) {
            this.openEventHandler = openEventHandler;
        }

        public void setCloseEventHandler(FrameCloseEventHandler closeEventHandler) {
            this.closeEventHandler = closeEventHandler;
        }

        public void clear() {
            for (int i = 0; i < buttons.length - 1; i++) {
                buttons[i] = null;
            }
        }

        public Inventory getInventory() {
            return instance;
        }

        public void show(Player viewer) {
            instance.clear();

            if (openEventHandler != null) {
                openEventHandler.onOpen(viewer);
            }

            for (int i = 0; i < size.size; i++) {
                if (buttons[i] == null)
                    continue;

                instance.setItem(i, buttons[i].getIS());
            }

            viewer.openInventory(instance);
            managerGUI.registerFrame(this);
        }

        public enum ChestSize {
            ONE(9), TWO(18), THREE(27), FOUR(36), FIVE(45), SIX(54);
            private final int size;

            private ChestSize(int size) {
                this.size = size;
            }

            public int getSize() {
                return size;
            }
        }

        public static class Builder {
            private ManagerGUI managerGUI;
            private Frame frame;
            private Player viewer;

            private Builder(ManagerGUI managerGUI, Player viewer, String title, ChestSize size) {
                this.managerGUI = managerGUI;
                this.frame = new Frame(managerGUI, title, size);
                this.viewer = viewer;
            }

            public static Builder newWith(ManagerGUI managerGUI, Player viewer, String title, ChestSize size) {
                return new Builder(managerGUI, viewer, title, size);
            }

            public Builder setButton(int row, int col, Button button) {
                frame.setButton(Button.getIndex(row, col), button);
                return this;
            }

            public Builder onOpenReact(FrameOpenEventHandler handler) {
                frame.openEventHandler = handler;
                return this;
            }

            public Builder onCloseReact(FrameCloseEventHandler handler) {
                frame.closeEventHandler = handler;
                return this;
            }

            public Frame build() {
                return frame;
            }
        }
    }

    public static class Button {
        private transient final ItemStack IS;
        private transient Frame parent;
        private transient ButtonEventHandler leftClickEventHandler;
        private transient ButtonEventHandler rightClickEventHandler;
        private transient ButtonEventHandler shiftLeftClickEventHandler;
        private transient ButtonEventHandler shiftRightClickEventHandler;

        protected Button(Frame parent, ItemStack IS) {
            this.parent = parent;
            this.IS = IS;
        }

        public static int getIndex(int row, int col) {
            return (row * 9) + col;
        }

        public Frame getParent() {
            return parent;
        }

        public void setParent(Frame parent) {
            this.parent = parent;
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

        public static class Builder {
            private Button button;

            private Builder(Frame parent, ItemStack IS) {
                button = new Button(parent, IS);
            }

            public static Builder with(Frame parent, Material material) {
                return new Builder(parent, new ItemStack(material));
            }

            public Builder amount(int amount) {
                button.IS.setAmount(amount);
                return this;
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

            public Builder onClickLeftShiftReact(ButtonEventHandler handler) {
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
}