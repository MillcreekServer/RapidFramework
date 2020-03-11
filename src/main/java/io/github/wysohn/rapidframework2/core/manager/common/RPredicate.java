package io.github.wysohn.rapidframework2.core.manager.common;

import java.util.function.Predicate;

public class RPredicate<T> implements Predicate<T> {
    private final Predicate<T> predicate;
    private FailHandle<T> onFail;
    private boolean showOnFail;

    RPredicate(Predicate<T> predicate, FailHandle<T> onFail, boolean shwoOnFail) {
        this.predicate = predicate;
        this.onFail = onFail;
        this.showOnFail = shwoOnFail;
    }

    private RPredicate(Predicate<T> predicate, FailHandle<T> onFail) {
        this(predicate, onFail, false);
    }

    public static <T> RPredicate<T> of(Predicate<T> predicate, FailHandle<T> onFail, boolean shwoOnFail) {
        return new RPredicate<T>(predicate, onFail, shwoOnFail);
    }

    public static <T> RPredicate<T> of(Predicate<T> predicate, FailHandle<T> onFail) {
        return new RPredicate<T>(predicate, onFail);
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