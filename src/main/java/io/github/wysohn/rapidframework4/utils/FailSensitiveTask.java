package io.github.wysohn.rapidframework4.utils;

import java.util.function.Supplier;

public class FailSensitiveTask extends FailSensitiveTaskGeneric<FailSensitiveTask, Boolean> {
    private FailSensitiveTask(Supplier<Boolean> task) {
        super(task, true);
    }

    public static FailSensitiveTask of(Supplier<Boolean> task) {
        return new FailSensitiveTask(task);
    }

    @Override
    public Boolean run() {
        Boolean result = super.run();
        return result != null && result;
    }
}
