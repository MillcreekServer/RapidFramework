package io.github.wysohn.rapidframework.pluginbase.objects.gui.frame;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.manager.gui.ManagerGUI;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.FirstButton;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.LastButton;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.NextButton;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.button.PreviousButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * Linked list style
 *
 * @author wysohn
 */
public class PageNodeFrame extends ManagerGUI.Frame {
    public final PageFrame manager;

    private final int index;
    private PageNodeFrame previous = null;
    private PageNodeFrame next = null;

    public PageNodeFrame(ManagerGUI managerGUI, PluginBase base, PageFrame manager, String name, int index) {
        super(managerGUI, name + " Pg." + index, ChestSize.SIX);
        this.manager = manager;
        this.index = index;

        // fill line 6
        // 0,3,5,8 - glass
        setButton(ManagerGUI.Button.getIndex(5, 0),
                ManagerGUI.Button.Builder.with(this, Material.STAINED_GLASS_PANE).build());
        setButton(ManagerGUI.Button.getIndex(5, 1), new FirstButton(base, this));
        setButton(ManagerGUI.Button.getIndex(5, 2), new PreviousButton(base, this));
        setButton(ManagerGUI.Button.getIndex(5, 3),
                ManagerGUI.Button.Builder.with(this, Material.STAINED_GLASS_PANE).build());
        setButton(ManagerGUI.Button.getIndex(5, 4),
                ManagerGUI.Button.Builder.with(this, Material.STAINED_GLASS_PANE)
                        .withDisplayName(ChatColor.GREEN + "Pg. " + index)
                        .build());
        setButton(ManagerGUI.Button.getIndex(5, 5),
                ManagerGUI.Button.Builder.with(this, Material.STAINED_GLASS_PANE).build());
        setButton(ManagerGUI.Button.getIndex(5, 6), new NextButton(base, this));
        setButton(ManagerGUI.Button.getIndex(5, 7), new LastButton(base, this));
        setButton(ManagerGUI.Button.getIndex(5, 8),
                ManagerGUI.Button.Builder.with(this, Material.STAINED_GLASS_PANE).build());
    }

    public int getIndex() {
        return index;
    }

    public PageNodeFrame getPrevious() {
        return previous;
    }

    void setPrevious(PageNodeFrame previous) {
        this.previous = previous;
    }

    public PageNodeFrame getNext() {
        return next;
    }

    void setNext(PageNodeFrame next) {
        this.next = next;
    }

}
