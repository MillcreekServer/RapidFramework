package io.github.wysohn.rapidframework2.core.manager.caching;

import java.util.Vector;

public abstract class ObservableElement {
    private transient final Vector<IObserver> observers;

    public ObservableElement() {
        observers = new Vector<>();
    }

    public void addObserver(IObserver observer) {
        if(observer == null)
            throw new NullPointerException("Observer cannot be null");

        if(!observers.contains(observer)){
            observers.add(observer);
        }
    }

    public synchronized void notifyObservers(){
        observers.forEach(iObserver -> iObserver.update(this));
    }
}