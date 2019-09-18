package io.github.wysohn.rapidframework.pluginbase.objects.gui.frame;

import io.github.wysohn.rapidframework.pluginbase.manager.gui.ManagerGUI;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameCloseEventHandler;
import io.github.wysohn.rapidframework.pluginbase.objects.gui.handlers.frame.FrameOpenEventHandler;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

public class ButtonPageFrame extends PageFrame {
    private final ManagerGUI.Button[] buttons;

    private final ManagerGUI.Button[][] pages;

    public ButtonPageFrame(ManagerGUI managerGUI, String name, ManagerGUI.Button[] items) {
        super(managerGUI, name);
        Validate.notNull(items);

        this.buttons = items;
        this.pages = divide(items);

        initButtonPages();
    }

    private final int MAXLENGTH = 5 * 9;

    private ManagerGUI.Button[][] divide(ManagerGUI.Button[] items) {
        int pagecount = items.length / MAXLENGTH;
        int leftover = items.length % MAXLENGTH;
        pagecount = leftover == 0 ? pagecount : pagecount + 1;

        ManagerGUI.Button[][] pages = new ManagerGUI.Button[pagecount][MAXLENGTH];

        for (int i = 0; i < items.length; i++) {
            pages[i / MAXLENGTH][i % MAXLENGTH] = items[i];
        }

        return pages;
    }

    public ManagerGUI.Button[] getItems() {
        return merge(pages);
    }

    private ManagerGUI.Button[] merge(ManagerGUI.Button[][] pages) {
        int pagecount = buttons.length / MAXLENGTH;
        int leftover = buttons.length % MAXLENGTH;
        pagecount = leftover == 0 ? pagecount : pagecount + 1;

        ManagerGUI.Button[] merged = new ManagerGUI.Button[pagecount * MAXLENGTH];

        for (int i = 0; i < pagecount * MAXLENGTH; i++) {
            merged[i] = pages[i / MAXLENGTH][i % MAXLENGTH];
        }

        return merged;
    }

    private void initButtonPages() {
        for (final ManagerGUI.Button[] page : pages) {
            final PageNodeFrame frame = add();
            frame.setOpenEventHandler(new FrameOpenEventHandler() {
                @Override
                public void onOpen(Player player) {
                    for (int i = 0; i < MAXLENGTH; i++) {
                        if (page[i] != null)
                            page[i].setParent(frame);
                        frame.setButton(i, page[i]);
                    }
                }
            });

            frame.setCloseEventHandler(new FrameCloseEventHandler() {
                @Override
                public void onClose(Player player) {
                    for (int i = 0; i < MAXLENGTH; i++) {
                        page[i] = frame.getButton(i);
                    }

                    ButtonPageFrame.this.closeHandler.onClose(player);
                }
            });
        }
    }
}
