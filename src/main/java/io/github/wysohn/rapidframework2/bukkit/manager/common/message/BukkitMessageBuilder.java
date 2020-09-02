package io.github.wysohn.rapidframework2.bukkit.manager.common.message;

import io.github.wysohn.rapidframework2.core.manager.common.message.MessageBuilder;
import io.github.wysohn.rapidframework2.tools.NMSWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class BukkitMessageBuilder extends MessageBuilder<ItemStack> {
    protected BukkitMessageBuilder(String str) {
        super(str);
    }

    public static BukkitMessageBuilder forBukkitMessage(String str) {
        return new BukkitMessageBuilder(str);
    }

    public BukkitMessageBuilder withHoverShowItem(ItemStack value) {
        message.resetHover();
        try {
            message.setHover_ShowItem(NMSWrapper.target(value.getClass()) // CraftItemStack
                    .prepare("asNMSCopy", ItemStack.class)
                    .invoke(value) // NMS ItemStack
                    .prepare("getTag")
                    .invoke() // NBTTagCompound
                    .result()
                    .map(Object::toString) // json
                    .orElseThrow(() -> new RuntimeException("NBTCompound to json failed.")));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }
}
