package io.github.wysohn.rapidframework.pluginbase.manager.item;

import org.bukkit.Material;

public class CustomItem {
    private Material type;

    public static class Builder {
	private CustomItem item;

	private Builder(Material material) {
	    item = new CustomItem();
	}

	public static Builder typeOf(Material material) {
	    return new Builder(material);
	}

	public CustomItem build() {
	    return item;
	}
    }
}
