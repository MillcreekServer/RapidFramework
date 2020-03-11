package io.github.wysohn.rapidframework2.core.interfaces;

import io.github.wysohn.rapidframework2.core.objects.location.SimpleChunkLocation;
import io.github.wysohn.rapidframework2.core.objects.location.SimpleLocation;

public interface ILocatable extends IPluginObject {
    SimpleLocation getSloc();

    SimpleChunkLocation getScloc();
}
