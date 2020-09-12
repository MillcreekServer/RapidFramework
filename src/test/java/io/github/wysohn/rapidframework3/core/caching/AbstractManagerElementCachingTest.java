package io.github.wysohn.rapidframework3.core.caching;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import io.github.wysohn.rapidframework3.core.database.Database;
import io.github.wysohn.rapidframework3.core.database.Databases;
import io.github.wysohn.rapidframework3.core.inject.module.ElementCachingManagerModule;
import io.github.wysohn.rapidframework3.core.interfaces.caching.IObserver;
import io.github.wysohn.rapidframework3.core.interfaces.serialize.ISerializer;
import io.github.wysohn.rapidframework3.core.main.PluginMain;
import io.github.wysohn.rapidframework3.modules.MockMainModule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class AbstractManagerElementCachingTest {
    private static Database mockDatabase;

    private MockMainModule mockMainModule;

    private List<Module> moduleList = new LinkedList<>();

    private TempManager manager;
    private IObserver observer;


    @Before
    public void init() {
        mockDatabase = Mockito.mock(Database.class);
        mockMainModule = new MockMainModule();
        when(mockMainModule.mockConfig.get(eq("dbType"))).thenReturn(Optional.of("file"));

        moduleList.add(new ElementCachingManagerModule<>(TempManager.class,
                TempValue.class, new TypeLiteral<ISerializer<TempValue>>() {
        }
        ));
        moduleList.add(mockMainModule);

        Injector injector = Guice.createInjector(moduleList);
        manager = injector.getInstance(TempManager.class);

        observer = manager.getObservers().stream().findFirst().orElse(null);
    }

    @Test
    public void testLoad() throws Exception {
        UUID[] uuids = new UUID[]{
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
        };

        when(mockDatabase.getKeys()).thenReturn(Arrays.stream(uuids)
                .map(UUID::toString)
                .collect(Collectors.toSet()));
        when(mockDatabase.load(Mockito.anyString())).then(invocation -> {
            String key = (String) invocation.getArguments()[0];

            for (int i = 0; i < uuids.length; i++) {
                if (key.equals(uuids[i].toString())) {
                    return "{\"str\":\"value" + i + "\"}";
                }
            }

            throw new RuntimeException("Test is not configured correctly.");
        });

        //start manager
        manager.enable();
        manager.load();

        Map<UUID, TempValue> cachedElements = (Map<UUID, TempValue>) Whitebox.getInternalState(manager, "cachedElements");
        Map<String, UUID> nameMap = (Map<String, UUID>) Whitebox.getInternalState(manager, "nameToKeyMap");
        List observers = (List) Whitebox.getInternalState(manager, "observers");

        for (int i = 0; i < uuids.length; i++) {
            assertEquals(manager, cachedElements.get(uuids[i]).manager);
            assertEquals("value" + i, cachedElements.get(uuids[i]).str);

            Mockito.verify(mockDatabase).load(eq(uuids[i].toString()));

            List<Observer> obs = (List<Observer>) Whitebox.getInternalState(cachedElements.get(uuids[i]), "observers");
            assertTrue(observers.stream().allMatch(obs::contains));
        }
    }

    @Singleton
    static class TempManager extends AbstractManagerElementCaching<UUID, TempValue> {
        @Inject
        public TempManager(PluginMain main, ISerializer<TempValue> serializer, Injector injector) {
            super(main, serializer, injector);
        }

        @Override
        protected Databases.DatabaseFactory createDatabaseFactory() {
            return (type) -> mockDatabase;
        }

        @Override
        protected UUID fromString(String string) {
            return UUID.fromString(string);
        }

        @Override
        protected TempValue newInstance(UUID key) {
            return new TempValue(key);
        }
    }

    static class TempValue extends CachedElement<UUID> {
        @Inject
        private TempManager manager;

        public Object dummy;
        private String str;

        public TempValue() {
            super(null);
        }

        public TempValue(UUID uuid) {
            super(uuid);
        }

        public TempValue(UUID uuid, String stringKey) {
            super(uuid);
            this.str = stringKey;
        }

        public TempValue(UUID uuid, IObserver observer) {
            super(uuid);
            this.addObserver(observer);
        }

        public TempValue setStr(String str) {
            this.str = str;

            notifyObservers();
            return this;
        }
    }
}