package io.github.wysohn.rapidframework3.interfaces;

import io.github.wysohn.rapidframework3.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework3.data.SimpleLocation;
import io.github.wysohn.rapidframework3.data.Vector;

public interface ILocatable extends IPluginObject {
    SimpleLocation getSloc();

    SimpleChunkLocation getScloc();

    Vector getDirection();
}
