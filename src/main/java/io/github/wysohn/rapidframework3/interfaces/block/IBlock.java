package io.github.wysohn.rapidframework3.interfaces.block;

import io.github.wysohn.rapidframework3.data.SimpleChunkLocation;
import io.github.wysohn.rapidframework3.data.SimpleLocation;
import io.github.wysohn.rapidframework3.interfaces.ILocatable;
import io.github.wysohn.rapidframework3.interfaces.IPluginObject;

public interface IBlock extends IPluginObject, ILocatable {
    SimpleLocation getSloc();

    SimpleChunkLocation getScloc();
}
