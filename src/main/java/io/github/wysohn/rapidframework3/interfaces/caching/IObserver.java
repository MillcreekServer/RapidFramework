package io.github.wysohn.rapidframework3.interfaces.caching;

import io.github.wysohn.rapidframework3.core.caching.AbstractManagerElementCaching;

public interface IObserver {
    void update(AbstractManagerElementCaching.ObservableElement observable);
}
