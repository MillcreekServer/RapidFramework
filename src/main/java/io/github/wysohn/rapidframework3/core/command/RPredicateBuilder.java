package io.github.wysohn.rapidframework3.core.command;

import java.util.function.Predicate;

public class RPredicateBuilder<T> {
    private Predicate<T> predicate;
    private RPredicate.FailHandle<T> onFail;
    private boolean shwoOnFail = false;

    private RPredicateBuilder(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    public static <T> RPredicateBuilder<T> of(Predicate<T> predicate) {
        return new RPredicateBuilder<>(predicate);
    }

    public RPredicateBuilder<T> onFail(RPredicate.FailHandle<T> onFail) {
        this.onFail = onFail;
        return this;
    }

    public RPredicateBuilder<T> showFail(boolean shwoOnFail) {
        this.shwoOnFail = shwoOnFail;
        return this;
    }

    public RPredicate<T> create() {
        return new RPredicate<>(predicate, onFail, shwoOnFail);
    }
}