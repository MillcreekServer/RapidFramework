package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.Group;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class ManagerGroupTest extends AbstractTest {
    UUID groupUuid;
    private ManagerGroup<PluginBase> managerGroup;

    @Before
    public void init() {
        super.init();

        managerGroup = new ManagerGroup<>(mockBase, 0);
    }

    @Test
    public void create() {
        UUID owner = UUID.randomUUID();

        Assert.assertNull(managerGroup.get(groupUuid));
        Assert.assertTrue(managerGroup.create("test", owner, group -> {
            groupUuid = group.getUuid();
            return true;
        }));
        Assert.assertFalse(managerGroup.create("test", UUID.randomUUID(), group -> true));

        Group group = managerGroup.get(groupUuid);
        Assert.assertNotNull(group);
        Assert.assertEquals(groupUuid, group.getUuid());
        Assert.assertEquals("test", group.getDisplayName());
    }

    @Test
    public void disband() {
    }

    @Test
    public void testDisband() {
    }

    static class TempGroup extends Group {
        public TempGroup(UUID leader) {
            super(leader);
        }
    }
}