package io.github.wysohn.rapidframework3.interfaces.io;

import java.io.InputStream;

public interface IPluginResourceProvider {
    /**
     * Get resource in jar file as stream. For Bukkit API, it's Bukkit.getResource(String).
     *
     * @param filename
     * @return
     */
    InputStream getResource(String filename);
}
