package io.github.wysohn.rapidframework4.interfaces.block;

import io.github.wysohn.rapidframework4.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework4.data.SimpleLocation;
import io.github.wysohn.rapidframework4.interfaces.ILocatable;
import io.github.wysohn.rapidframework4.interfaces.IPluginObject;

public interface IBlock extends IPluginObject, ILocatable {
    SimpleLocation getSloc();

    SimpleChunkLocation getScloc();
}
