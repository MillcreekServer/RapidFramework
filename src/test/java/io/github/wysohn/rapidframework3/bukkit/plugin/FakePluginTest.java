package io.github.wysohn.rapidframework3.bukkit.plugin;

import io.github.wysohn.rapidframework3.bukkit.testutils.AbstractBukkitTest;
import io.github.wysohn.rapidframework3.bukkit.testutils.SimpleBukkitPluginMainTest;
import org.bukkit.Server;
import org.junit.Test;

public class FakePluginTest extends AbstractBukkitTest {

    @Test
    public void test() {
        new SimpleBukkitPluginMainTest<FakePlugin>() {
            @Override
            public FakePlugin instantiate(Server server) {
                return new FakePlugin(server);
            }
        }.enable();
    }
}