package io.github.wysohn.rapidframework2.core.interfaces.block;

import io.github.wysohn.rapidframework2.core.interfaces.ILocatable;
import io.github.wysohn.rapidframework2.core.interfaces.IPluginObject;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;

public interface IBlock extends IPluginObject, ILocatable {
    SimpleLocation getSloc();

    SimpleChunkLocation getScloc();
}
