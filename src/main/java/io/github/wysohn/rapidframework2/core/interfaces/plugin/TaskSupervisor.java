package io.github.wysohn.rapidframework2.core.interfaces.plugin;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface TaskSupervisor {
    <T> Future<T> runAsync(Callable<T> callable);

    <T> Future<T> runSync(Callable<T> callable);
}