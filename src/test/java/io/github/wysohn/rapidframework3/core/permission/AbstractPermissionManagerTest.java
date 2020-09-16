package io.github.wysohn.rapidframework3.core.permission;

import com.google.inject.*;
import io.github.wysohn.rapidframework3.core.database.Database;
import io.github.wysohn.rapidframework3.core.database.Databases;
import io.github.wysohn.rapidframework3.core.inject.module.GsonSerializerModule;
import io.github.wysohn.rapidframework3.core.language.DefaultLangs;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.interfaces.language.ILang;
import io.github.wysohn.rapidframework3.interfaces.permissin.IParentProvider;
import io.github.wysohn.rapidframework3.interfaces.permissin.IPermission;
import io.github.wysohn.rapidframework3.interfaces.permissin.IPermissionHolder;
import io.github.wysohn.rapidframework3.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.testmodules.MockMainModule;
import io.github.wysohn.rapidframework3.testmodules.MockParentProviderModule;
import io.github.wysohn.rapidframework3.testmodules.MockPluginDirectoryModule;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AbstractPermissionManagerTest {

    private static Database mockDatabase;

    private List<Module> moduleList = new LinkedList<>();
    private MockParentProviderModule parentProviderModule;
    private MockMainModule mockMainModule;

    @Before
    public void init() throws Exception {
        mockDatabase = mock(Database.class);
        parentProviderModule = new MockParentProviderModule();
        mockMainModule = new MockMainModule();

        moduleList.add(new GsonSerializerModule());
        moduleList.add(mockMainModule);
        moduleList.add(new MockPluginDirectoryModule());
        moduleList.add(parentProviderModule);
    }

    @Test
    public void addAndHasPermission() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempPermissionManager manager = injector.getInstance(TempPermissionManager.class);

        manager.enable();

        UUID holderUuid = UUID.randomUUID();
        UUID parentUuid = UUID.randomUUID();
        UUID parentParentUuid = UUID.randomUUID();

        IPermissionHolder mockHolder = mock(IPermissionHolder.class);
        IPermissionHolder mockParent = mock(IPermissionHolder.class);
        IPermissionHolder mockParentParent = mock(IPermissionHolder.class);

        when(mockHolder.getUuid()).thenReturn(holderUuid);
        when(mockParent.getUuid()).thenReturn(parentUuid);
        when(mockParentParent.getUuid()).thenReturn(parentParentUuid);
        when(mockHolder.getParentUuid()).thenReturn(parentUuid);
        when(mockParent.getParentUuid()).thenReturn(parentParentUuid);

        manager.addPermission(mockHolder, Perms.SomePerm);
        manager.addPermission(mockParent, Perms.SomePerm2);
        manager.addPermission(mockParentParent, Perms.SomePerm3);

        IParentProvider mockProvider = parentProviderModule.getParentProvider();
        when(mockProvider.getHolder(eq(parentUuid))).thenReturn(mockParent);
        when(mockProvider.getHolder(eq(parentParentUuid))).thenReturn(mockParentParent);

        //holder
        assertTrue(manager.hasPermission(mockHolder, Perms.SomePerm));
        //holder->parent->parentParent->null
        assertFalse(manager.hasPermission(mockHolder, Perms.SomePerm4));
        //holder->parent
        assertTrue(manager.hasPermission(mockHolder, Perms.SomePerm2));
        //holder->parent->parent
        assertTrue(manager.hasPermission(mockHolder, Perms.SomePerm3));

        verify(mockProvider, times(3))
                .getHolder(mockHolder.getParentUuid());
        verify(mockProvider, times(2))
                .getHolder(mockParent.getParentUuid());

        //multiple permissions
        assertTrue(manager.hasPermission(mockHolder,
                Perms.SomePerm4, Perms.SomePerm3, Perms.SomePerm2, Perms.SomePerm));

        manager.disable();
    }

    @Test
    public void removePermission() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempPermissionManager manager = injector.getInstance(TempPermissionManager.class);

        manager.enable();

        UUID holderUuid = UUID.randomUUID();
        IPermissionHolder mockHolder = mock(IPermissionHolder.class);
        when(mockHolder.getUuid()).thenReturn(holderUuid);

        manager.addPermission(mockHolder, Perms.SomePerm);
        assertTrue(manager.hasPermission(mockHolder, Perms.SomePerm));
        manager.removePermission(mockHolder, Perms.SomePerm);
        assertFalse(manager.hasPermission(mockHolder, Perms.SomePerm));

        manager.disable();
    }

    @Test
    public void resetPermission() throws Exception {
        Injector injector = Guice.createInjector(moduleList);
        TempPermissionManager manager = injector.getInstance(TempPermissionManager.class);

        manager.enable();

        UUID holderUuid = UUID.randomUUID();
        IPermissionHolder mockHolder = mock(IPermissionHolder.class);
        when(mockHolder.getUuid()).thenReturn(holderUuid);

        Arrays.stream(Perms.values()).forEach(p -> manager.addPermission(mockHolder, p));
        Arrays.stream(Perms.values()).forEach(p -> assertTrue(manager.hasPermission(mockHolder, p)));
        manager.resetPermission(mockHolder);
        Arrays.stream(Perms.values()).forEach(p -> assertFalse(manager.hasPermission(mockHolder, p)));

        manager.disable();
    }

    enum Perms implements IPermission {
        SomePerm(UUID.fromString("ce04af26-0533-4d20-81b6-183bca8107c1"),
                DefaultLangs.Structure_Title, DefaultLangs.Structure_Title),
        SomePerm2(UUID.fromString("1467f8bb-7426-4f36-870f-839ee64b210f"),
                DefaultLangs.Structure_Title, DefaultLangs.Structure_Title),
        SomePerm3(UUID.fromString("45640d6a-5ace-4552-b755-eab2f074d8fc"),
                DefaultLangs.Structure_Title, DefaultLangs.Structure_Title),
        SomePerm4(UUID.fromString("d0756229-d4d5-46c1-8293-ccbc2bffd58d"),
                DefaultLangs.Structure_Title, DefaultLangs.Structure_Title),
        ;

        private final UUID uuid;
        private final ILang name;
        private final ILang desc;

        Perms(UUID uuid, ILang name, ILang desc) {
            this.uuid = uuid;
            this.name = name;
            this.desc = desc;
        }

        @Override
        public UUID getUuid() {
            return uuid;
        }
    }

    @Singleton
    public static class TempPermissionManager extends AbstractPermissionManager {
        @Inject
        public TempPermissionManager(PluginMain main,
                                     ISerializer serializer,
                                     Injector injector,
                                     IParentProvider parentProvider) {
            super(main, serializer, injector, parentProvider);
        }

        @Override
        protected Databases.DatabaseFactory createDatabaseFactory() {
            return (type) -> mockDatabase;
        }

        @Override
        protected PermissionStorage newInstance(UUID key) {
            return new PermissionStorage(key);
        }
    }
}