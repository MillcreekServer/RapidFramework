package io.github.wysohn.rapidframework3.core.database.migration;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

public interface IMigrationStep<FROM extends CachedElement<?>,
        TO extends CachedElement<?>> {
    void migrate(FROM from, TO to);
}
