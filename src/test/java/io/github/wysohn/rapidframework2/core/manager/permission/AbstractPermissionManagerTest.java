package io.github.wysohn.rapidframework2.core.manager.permission;

import io.github.wysohn.rapidframework2.core.database.Database;
import io.github.wysohn.rapidframework2.core.interfaces.entity.IPermissionHolder;
import io.github.wysohn.rapidframework2.core.manager.lang.DefaultLangs;
import io.github.wysohn.rapidframework2.core.manager.lang.DynamicLang;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.UUID;

public class AbstractPermissionManagerTest {

    enum Perms implements IPermission{
        SomePerm(UUID.fromString("ce04af26-0533-4d20-81b6-183bca8107c1"),
                new DynamicLang(DefaultLangs.Structure_Title), new DynamicLang(DefaultLangs.Structure_Title)),
        SomePerm2(UUID.fromString("1467f8bb-7426-4f36-870f-839ee64b210f"),
                new DynamicLang(DefaultLangs.Structure_Title), new DynamicLang(DefaultLangs.Structure_Title)),
        SomePerm3(UUID.fromString("45640d6a-5ace-4552-b755-eab2f074d8fc"),
                new DynamicLang(DefaultLangs.Structure_Title), new DynamicLang(DefaultLangs.Structure_Title)),
        SomePerm4(UUID.fromString("d0756229-d4d5-46c1-8293-ccbc2bffd58d"),
                new DynamicLang(DefaultLangs.Structure_Title), new DynamicLang(DefaultLangs.Structure_Title)),
        ;

        private final UUID uuid;
        private final DynamicLang name;
        private final DynamicLang desc;

        Perms(UUID uuid, DynamicLang name, DynamicLang desc) {
            this.uuid = uuid;
            this.name = name;
            this.desc = desc;
        }

        @Override
        public DynamicLang getName() {
            return name;
        }

        @Override
        public DynamicLang getDescription() {
            return desc;
        }

        @Override
        public UUID getUuid() {
            return uuid;
        }
    }

    private IParentProvider mockProvider;
    private AbstractPermissionManager abstractPermissionManager;

    @Before
    public void init() throws Exception{
        mockProvider = Mockito.mock(IParentProvider.class);
        abstractPermissionManager = new AbstractPermissionManager(0, mockProvider){
            @Override
            protected Database.DatabaseFactory<PermissionStorage> createDatabaseFactory() {
                return null;
            }
        };

        abstractPermissionManager.load();
    }

    @Test
    public void addAndHasPermission() throws Exception {
        UUID holderUuid = UUID.randomUUID();
        UUID parentUuid = UUID.randomUUID();
        UUID parentParentUuid = UUID.randomUUID();

        IPermissionHolder mockHolder = Mockito.mock(IPermissionHolder.class);
        IPermissionHolder mockParent = Mockito.mock(IPermissionHolder.class);
        IPermissionHolder mockParentParent = Mockito.mock(IPermissionHolder.class);

        Mockito.when(mockHolder.getUuid()).thenReturn(holderUuid);
        Mockito.when(mockParent.getUuid()).thenReturn(parentUuid);
        Mockito.when(mockParentParent.getUuid()).thenReturn(parentParentUuid);
        Mockito.when(mockHolder.getParentUuid()).thenReturn(parentUuid);
        Mockito.when(mockParent.getParentUuid()).thenReturn(parentParentUuid);

        abstractPermissionManager.addPermission(mockHolder, Perms.SomePerm);
        abstractPermissionManager.addPermission(mockParent, Perms.SomePerm2);
        abstractPermissionManager.addPermission(mockParentParent, Perms.SomePerm3);

        Mockito.when(mockProvider.getHolder(Mockito.any(), Mockito.eq(parentUuid))).thenReturn(mockParent);
        Mockito.when(mockProvider.getHolder(Mockito.any(), Mockito.eq(parentParentUuid))).thenReturn(mockParentParent);

        //holder
        Assert.assertTrue(abstractPermissionManager.hasPermission(mockHolder, Perms.SomePerm));
        //holder->parent->parentParent->null
        Assert.assertFalse(abstractPermissionManager.hasPermission(mockHolder, Perms.SomePerm4));
        //holder->parent
        Assert.assertTrue(abstractPermissionManager.hasPermission(mockHolder, Perms.SomePerm2));
        //holder->parent->parent
        Assert.assertTrue(abstractPermissionManager.hasPermission(mockHolder, Perms.SomePerm3));

        Mockito.verify(mockProvider, Mockito.times(3))
                .getHolder(null, mockHolder.getParentUuid());
        Mockito.verify(mockProvider, Mockito.times(2))
                .getHolder(null, mockParent.getParentUuid());

        //multiple permissions
        Assert.assertTrue(abstractPermissionManager.hasPermission(mockHolder,
                Perms.SomePerm4, Perms.SomePerm3, Perms.SomePerm2, Perms.SomePerm));

        abstractPermissionManager.disable();
    }

    @Test
    public void removePermission() throws Exception{
        UUID holderUuid = UUID.randomUUID();
        IPermissionHolder mockHolder = Mockito.mock(IPermissionHolder.class);
        Mockito.when(mockHolder.getUuid()).thenReturn(holderUuid);

        abstractPermissionManager.addPermission(mockHolder, Perms.SomePerm);
        Assert.assertTrue(abstractPermissionManager.hasPermission(mockHolder, Perms.SomePerm));
        abstractPermissionManager.removePermission(mockHolder, Perms.SomePerm);
        Assert.assertFalse(abstractPermissionManager.hasPermission(mockHolder, Perms.SomePerm));

        abstractPermissionManager.disable();
    }

    @Test
    public void resetPermission() throws Exception{
        UUID holderUuid = UUID.randomUUID();
        IPermissionHolder mockHolder = Mockito.mock(IPermissionHolder.class);
        Mockito.when(mockHolder.getUuid()).thenReturn(holderUuid);

        Arrays.stream(Perms.values()).forEach(p -> abstractPermissionManager.addPermission(mockHolder, p));
        Arrays.stream(Perms.values()).forEach(p -> Assert.assertTrue(abstractPermissionManager.hasPermission(mockHolder, p)));
        abstractPermissionManager.resetPermission(mockHolder);
        Arrays.stream(Perms.values()).forEach(p -> Assert.assertFalse(abstractPermissionManager.hasPermission(mockHolder, p)));

        abstractPermissionManager.disable();
    }
}