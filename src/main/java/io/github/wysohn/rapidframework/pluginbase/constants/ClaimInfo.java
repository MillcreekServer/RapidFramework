package io.github.wysohn.rapidframework.pluginbase.constants;

import io.github.wysohn.rapidframework.pluginbase.manager.ManagerElementCaching.NamedElement;

import java.util.Set;
import java.util.UUID;

public interface ClaimInfo extends NamedElement {

    @Override
    String getName();

    void setArea(Area area);

    Area getArea();

    void setPublic(boolean bool);

    boolean isPublic();

    UUID getOwner();

    void setOwner(UUID uuid);

    Set<UUID> getTrusts();

}