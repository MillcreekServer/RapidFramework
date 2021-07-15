package io.github.wysohn.rapidframework3.core.database.migration;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MigrationSteps<K, FROM extends CachedElement<K>, TO extends CachedElement<K>>
    implements IMigrationStep<FROM, TO>{
    private Runnable before;
    private Runnable after;
    private final List<IMigrationStep<FROM, TO>> stepList = new LinkedList<>();

    @Override
    public void migrate(FROM from, TO to) {
        try{
            Optional.ofNullable(before).ifPresent(Runnable::run);
            stepList.forEach(step -> step.migrate(from, to));
        } finally {
            Optional.ofNullable(after).ifPresent(Runnable::run);
        }
    }

    public static class Builder<K, FROM extends CachedElement<K>, TO extends CachedElement<K>>{
        private final MigrationSteps<K, FROM, TO> steps = new MigrationSteps<>();

        private Builder(){

        }

        public static <K, FROM extends CachedElement<K>, TO extends CachedElement<K>> Builder<K, FROM, TO> begin(){
            return new Builder<>();
        }

        public Builder<K, FROM, TO> before(Runnable before){
            steps.before = before;
            return this;
        }

        public Builder<K, FROM, TO> after(Runnable after){
            steps.after = after;
            return this;
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
