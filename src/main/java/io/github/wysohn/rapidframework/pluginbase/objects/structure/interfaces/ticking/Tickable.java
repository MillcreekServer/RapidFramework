package io.github.wysohn.rapidframework.pluginbase.objects.structure.interfaces.ticking;

public interface Tickable {
    /**
     * Called asynchronously before onTick() is called. Useful when you don't want to
     * waste CPU time of the server thread.
     *
     * @param tick the current tick. Increases by one each tick.
     */
    void onPreTickAsync(long tick);

    /**
     * Called synchronously. Will be held by the server thread. Blocking task in this method
     * will lag the server.
     *
     * @param tick the current tick. Increases by one each tick.
     */
    void onTick(long tick);
}
