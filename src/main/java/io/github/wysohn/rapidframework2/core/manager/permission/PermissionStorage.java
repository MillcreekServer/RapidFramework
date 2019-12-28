package io.github.wysohn.rapidframework2.core.manager.permission;

import io.github.wysohn.rapidframework2.core.interfaces.plugin.manager.NamedElement;

import java.util.HashSet;
import java.util.UUID;

public class PermissionStorage extends HashSet<UUID> implements NamedElement {
    @Override
    public String getName() {
        return null;
    }
}
