package io.github.wysohn.rapidframework3.bukkit.plugin;

import io.github.wysohn.rapidframework3.bukkit.testutils.AbstractBukkitTest;
import org.junit.Test;

public class FakePluginTest extends AbstractBukkitTest {

    @Test
    public void onLoad() {
        FakePlugin fakePlugin = new FakePlugin(mockPluginLoader());
        fakePlugin.onLoad();
    }

    @Test
    public void onEnable() {
        FakePlugin fakePlugin = new FakePlugin(mockPluginLoader());
        fakePlugin.onLoad();
        fakePlugin.onEnable();
    }

    @Test
    public void onDiable() {
        FakePlugin fakePlugin = new FakePlugin(mockPluginLoader());
        fakePlugin.onLoad();
        fakePlugin.onEnable();
        fakePlugin.onDisable();
    }
}