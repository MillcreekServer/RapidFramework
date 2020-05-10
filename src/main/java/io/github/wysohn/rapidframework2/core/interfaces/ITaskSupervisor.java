package io.github.wysohn.rapidframework2.core.interfaces;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface ITaskSupervisor {
    <V> Future<V> sync(Callable<V> callable);

    void sync(Runnable runnable);

    <V> Future<V> async(Callable<V> callable);

    void async(Runnable runnable);
}
