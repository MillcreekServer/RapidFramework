package io.github.wysohn.rapidframework3.core.database.migration;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.util.LinkedList;
import java.util.List;

public class MigrationSteps<K, FROM extends CachedElement<K>, TO extends CachedElement<K>>
    implements IMigrationStep<FROM, TO>{
    private final List<IMigrationStep<FROM, TO>> stepList = new LinkedList<>();

    @Override
    public void migrate(FROM from, TO to) {
        stepList.forEach(step -> step.migrate(from, to));
    }

    public static class Builder<K, FROM extends CachedElement<K>, TO extends CachedElement<K>>{
        private final MigrationSteps<K, FROM, TO> steps = new MigrationSteps<>();

        private Builder(){

        }

        public static <K, FROM extends CachedElement<K>, TO extends CachedElement<K>> Builder<K, FROM, TO> begin(){
            return new Builder<K, FROM, TO>();
        }

        public Builder<K, FROM, TO> step(IMigrationStep<FROM, TO> step){
            steps.stepList.add(step);
            return this;
        }

        public MigrationSteps<K, FROM, TO> build(){
            return steps;
        }
    }
}
