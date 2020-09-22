package io.github.wysohn.rapidframework3.interfaces.entity;

import io.github.wysohn.rapidframework3.interfaces.IMemento;

public interface IEntitySnapshot {
    IMemento saveState();

    void restoreState(IMemento savedState);
}
