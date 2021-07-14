package io.github.wysohn.rapidframework3.interfaces.caching;

import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;

public interface IObserver {
    /**
     * Invoked by {@link io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching.ObservableElement}
     * to notify 'this' observer. Note that ObservableElement calls this method
     * while holding the write-lock, so be-careful about nesting the locks.
     * @param observable element that updated its state
     */
    void update(AbstractManagerElementCaching.ObservableElement observable);
}
