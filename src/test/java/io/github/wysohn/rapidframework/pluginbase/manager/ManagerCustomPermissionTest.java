package io.github.wysohn.rapidframework.pluginbase.manager;

import io.github.wysohn.rapidframework.pluginbase.PluginBase;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.Permission;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolder;
import io.github.wysohn.rapidframework.pluginbase.objects.permissions.PermissionHolderProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class ManagerCustomPermissionTest extends AbstractTest {
    @Test
    public void hasPermissionAtLesat() {
        PermissionHolder mockHolder = Mockito.mock(PermissionHolder.class);
        PermissionHolder mockHolderParent = Mockito.mock(PermissionHolder.class);
        UUID uuid2 = UUID.randomUUID();
        PermissionHolder mockHolderParentParent = Mockito.mock(PermissionHolder.class);
        UUID uuid3 = UUID.randomUUID();

        Mockito.when(mockHolder.getParentUuid()).thenReturn(uuid2);
        Mockito.when(mockHolderParent.getParentUuid()).thenReturn(uuid3);

        ManagerCustomPermission<PluginBase> managerCustomPermission = new ManagerCustomPermission<>(
                mockBase,
                0
        );

        managerCustomPermission.registerProvider(new PermissionHolderProvider() {
            @Override
            public PermissionHolder getPermissionHolder(UUID uuid) {
                if (uuid.equals(uuid2)) {
                    return mockHolderParent;
                } else if (uuid.equals(uuid3)) {
                    return mockHolderParentParent;
                } else {
                    return mockHolder;
                }
            }
        });

        Mockito.when(mockHolder.hasPermission(Mockito.any(TempPermission.class))).thenReturn(true);
        Assert.assertTrue(managerCustomPermission.hasPermissionAtLesat(mockHolder, TempPermission.A));

        Mockito.when(mockHolder.hasPermission(Mockito.any(TempPermission.class))).thenReturn(false);
        Assert.assertFalse(managerCustomPermission.hasPermissionAtLesat(mockHolder, TempPermission.A));

        Mockito.when(mockHolder.hasPermission(Mockito.any(TempPermission.class))).thenReturn(false);
        Mockito.when(mockHolderParent.hasPermission(Mockito.any(TempPermission.class))).thenReturn(true);
        Assert.assertTrue(managerCustomPermission.hasPermissionAtLesat(mockHolder, TempPermission.A));

        Mockito.when(mockHolder.hasPermission(Mockito.any(TempPermission.class))).thenReturn(false);
        Mockito.when(mockHolderParent.hasPermission(Mockito.any(TempPermission.class))).thenReturn(false);
        Mockito.when(mockHolderParentParent.hasPermission(Mockito.any(TempPermission.class))).thenReturn(true);
        Assert.assertTrue(managerCustomPermission.hasPermissionAtLesat(mockHolder, TempPermission.A));
    }

    enum TempPermission implements Permission {
        A;
    }
}