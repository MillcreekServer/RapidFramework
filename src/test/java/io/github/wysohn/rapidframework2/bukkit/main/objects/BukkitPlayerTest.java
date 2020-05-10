package io.github.wysohn.rapidframework2.bukkit.main.objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class})
public class BukkitPlayerTest {
    PlayerInventory mockInventory;
    ItemStack[] items = new ItemStack[9 * 6];

    Player mockPlayer;
    UUID uuid;

    ItemFactory mockItemFactory;
    private BukkitPlayer bukkitPlayer;

    @Before
    public void init() {
        mockInventory = mock(PlayerInventory.class);
        when(mockInventory.addItem(any())).then(invocation -> {
            Object[] args = invocation.getArguments();
            ItemStack[] items = new ItemStack[args.length];
            System.arraycopy(args, 0, items, 0, args.length);

            return addItem(items);
        });
        when(mockInventory.getContents()).thenReturn(items);

        mockPlayer = mock(Player.class);
        when(mockPlayer.getInventory()).thenReturn(mockInventory);
        when(mockPlayer.getUniqueId()).thenReturn(uuid);

        bukkitPlayer = new BukkitPlayer(uuid);
        bukkitPlayer.sender = mockPlayer;

        mockItemFactory = mock(ItemFactory.class);
        when(mockItemFactory.equals(any(), any())).then(invocation -> {
            ItemMeta a = (ItemMeta) invocation.getArguments()[0];
            ItemMeta b = (ItemMeta) invocation.getArguments()[1];
            return true;
        });

        PowerMockito.mockStatic(Bukkit.class);
        PowerMockito.when(Bukkit.getItemFactory()).thenReturn(mockItemFactory);
    }

    public int firstEmpty() {
        ItemStack[] inventory = items;
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int firstPartial(ItemStack item) {
        ItemStack[] inventory = items;
        ItemStack filteredItem = new ItemStack(item);
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length; i++) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getType() == filteredItem.getType() && cItem.getAmount() < cItem.getMaxStackSize() && cItem.getDurability() == filteredItem.getDurability() && cItem.getEnchantments().equals(filteredItem.getEnchantments())) {
                return i;
            }
        }
        return -1;
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

        /* TODO: some optimization
         *  - Create a 'firstPartial' with a 'fromIndex'
         *  - Record the lastPartial per Material
         *  - Cache firstEmpty result
         */

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            while (true) {
                // Do we already have a stack of it?
                int firstPartial = firstPartial(item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > getMaxItemStack()) {
                            ItemStack stack = new ItemStack(item.getType(), getMaxItemStack(), item.getDurability());
                            stack.addUnsafeEnchantments(item.getEnchantments());
                            setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - getMaxItemStack());
                        } else {
                            // Just store it
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    ItemStack partialItem = getItem(firstPartial);

                    int amount = item.getAmount();
                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    private ItemStack getItem(int firstPartial) {
        return items[firstPartial];
    }

    private void setItem(int firstFree, ItemStack stack) {
        items[firstFree] = stack;
    }

    private int getMaxItemStack() {
        return 64;
    }

    @Test
    public void give() {
        assertEquals(0, bukkitPlayer.give(Material.DIAMOND, 130));
        assertEquals(new ItemStack(Material.DIAMOND, 64), items[0]);
        assertEquals(new ItemStack(Material.DIAMOND, 64), items[1]);
        assertEquals(new ItemStack(Material.DIAMOND, 2), items[2]);
    }

    @Test
    public void give2() {
        for (int i = 0; i < items.length; i++)
            items[i] = new ItemStack(Material.COBBLESTONE);
        items[30] = new ItemStack(Material.DIAMOND, 10);
        items[31] = new ItemStack(Material.DIAMOND, 30);
        // only 88 (128 - 10 - 30) space left

        assertEquals(130 - 88, bukkitPlayer.give(Material.DIAMOND, 130));
        assertEquals(new ItemStack(Material.DIAMOND, 64), items[30]);
        assertEquals(new ItemStack(Material.DIAMOND, 64), items[31]);
    }

    @Test
    public void give3() {
        for (int i = 0; i < items.length; i++)
            items[i] = new ItemStack(Material.COBBLESTONE);

        assertEquals(130, bukkitPlayer.give(Material.DIAMOND, 130));
        for (ItemStack item : items)
            assertEquals(new ItemStack(Material.COBBLESTONE), item);
    }

    @Test
    public void take() {
        assertEquals(80, bukkitPlayer.take(Material.DIAMOND, 80));
    }

    @Test
    public void take2() {
        for (int i = 0; i < items.length; i++)
            items[i] = new ItemStack(Material.COBBLESTONE);
        items[5] = new ItemStack(Material.DIAMOND, 50);

        assertEquals(80 - 50, bukkitPlayer.take(Material.DIAMOND, 80));
    }

    @Test
    public void take3() {
        for (int i = 0; i < items.length; i++)
            items[i] = new ItemStack(Material.COBBLESTONE);
        items[5] = new ItemStack(Material.DIAMOND, 20);
        items[6] = new ItemStack(Material.DIAMOND, 30);
        items[7] = new ItemStack(Material.DIAMOND, 10);
        items[8] = new ItemStack(Material.DIAMOND, 5);
        items[9] = new ItemStack(Material.DIAMOND, 5);
        items[10] = new ItemStack(Material.DIAMOND, 10);

        assertEquals(0, bukkitPlayer.take(Material.DIAMOND, 80));
    }
}