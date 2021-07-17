package io.github.wysohn.rapidframework4.modules;

import java.util.logging.*;

/**
 * https://garbagecollected.org/2007/12/08/guice-debug-output/
 * Enable or disable Guice debug output
 * on the console.
 */
public class GuiceDebug {
    private static final Handler HANDLER;

    static {
        HANDLER = new StreamHandler(System.out, new Formatter() {
            public String format(LogRecord record) {
                return String.format("[Guice %s] %s%n",
                        record.getLevel().getName(),
                        record.getMessage());
            }
        });
        HANDLER.setLevel(Level.WARNING);
    }

    private GuiceDebug() {
    }

    public static Logger getLogger() {
        return Logger.getLogger("com.google.inject");
    }

    public static void enable() {
        Logger guiceLogger = getLogger();
        guiceLogger.addHandler(GuiceDebug.HANDLER);
        guiceLogger.setLevel(Level.ALL);
    }

    public static void disable() {
        Logger guiceLogger = getLogger();
        guiceLogger.setLevel(Level.OFF);
        guiceLogger.removeHandler(GuiceDebug.HANDLER);
    }
}