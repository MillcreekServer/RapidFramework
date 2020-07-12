package io.github.wysohn.rapidframework2.core.manager.caching;

import java.util.Vector;

public abstract class ObservableElement {
    private transient final Vector<IObserver> observers;

    public ObservableElement() {
        observers = new Vector<>();
    }

    void addObserver(IObserver observer) {
        if (observer == null)
            throw new NullPointerException("Observer cannot be null");

        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    void removeObserver(IObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    protected synchronized void notifyObservers() {
        if (observers.size() < 1) {
            throw new RuntimeException("An ObservableElement invoked notifyObservers() method, yet no observers" +
                    " are found. Probably this instance was unregistered when delete() method was used. Do not" +
                    " use the instance that was deleted. Always retrieve the latest instance by get() method.");
        }
        observers.forEach(iObserver -> iObserver.update(this));
    }
}