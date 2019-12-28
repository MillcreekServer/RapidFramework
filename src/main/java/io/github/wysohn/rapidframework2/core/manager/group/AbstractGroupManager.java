package io.github.wysohn.rapidframework2.core.manager.group;

import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.interfaces.entity.ICommandSender;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPlayerWrapper;
import io.github.wysohn.rapidframework2.core.manager.common.AbstractManagerElementCaching;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;

import java.util.UUID;

public abstract class AbstractGroupManager<V extends Group> extends AbstractManagerElementCaching<UUID, V> {
    public AbstractGroupManager(int loadPriority) {
        super(loadPriority);
    }

    @Override
    protected UUID fromString(String string) {
        return UUID.fromString(string);
    }
}
