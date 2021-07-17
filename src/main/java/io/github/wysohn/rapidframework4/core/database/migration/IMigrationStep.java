package io.github.wysohn.rapidframework4.core.database.migration;

import io.github.wysohn.rapidframework4.core.caching.CachedElement;

public interface IMigrationStep<FROM extends CachedElement<?>,
        TO extends CachedElement<?>> {
    /**
     * Single step of migration to be performed.
     *
     * Note that you should not modify the FROM as this is a read-only
     * object. Modifying the FROM object highly likely will throw exception
     * as observers are not registered for the object we are reading from.
     * @param from
     * @param to
     */
    void migrate(FROM from, TO to);
}
