package io.github.wysohn.rapidframework4.interfaces;

import io.github.wysohn.rapidframework4.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework4.data.SimpleLocation;
import io.github.wysohn.rapidframework4.data.Vector;

public interface ILocatable extends IPluginObject {
    SimpleLocation getSloc();

    SimpleChunkLocation getScloc();

    Vector getDirection();
}
