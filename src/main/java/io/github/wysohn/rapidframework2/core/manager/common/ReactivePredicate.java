package io.github.wysohn.rapidframework2.core.manager.common;

import java.util.function.Predicate;

public class ReactivePredicate<T> implements Predicate<T> {
    private final Predicate<T> predicate;
    private final FailHandle<T> onFail;
    private final boolean showOnFail;

    public ReactivePredicate(Predicate<T> predicate, FailHandle<T> onFail, boolean shwoOnFail) {
        this.predicate = predicate;
        this.onFail = onFail;
        this.showOnFail = shwoOnFail;
    }

    public ReactivePredicate(Predicate<T> predicate, FailHandle<T> onFail) {
        this(predicate, onFail, false);
    }

    @Override
    public boolean test(T t) {
        if (predicate.test(t)){
            return true;
        } else {
            if(showOnFail){
                onFail.onFail(t);
            }
            return false;
        }
    }

    public boolean testWithMessage(T t){
        if (predicate.test(t)) {
            return true;
        } else {
            onFail.onFail(t);
            return false;
        }
    }

    public interface FailHandle<T>{
        void onFail(T val);
    }
}