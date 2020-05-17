package io.github.wysohn.rapidframework2.core.manager.caching;

import io.github.wysohn.rapidframework2.core.interfaces.IPluginObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SimpleInstanceProviderTest {

    private AbstractManagerElementCaching<UUID, Value> mockManager1;
    private AbstractManagerElementCaching<UUID, Value2> mockManager2;
    private AbstractManagerElementCaching<UUID, Value3> mockManager3;

    private Optional<WeakReference<Value>> optVal1;
    private Optional<WeakReference<Value2>> optVal2;
    private Optional<WeakReference<Value3>> optVal3;
    private Value mockVal1;
    private Value2 mockVal2;
    private Value3 mockVal3;
    private UUID uuid1;
    private UUID uuid2;
    private UUID uuid3;

    @Before
    public void init() {
        mockManager1 = mock(AbstractManagerElementCaching.class);
        mockManager2 = mock(AbstractManagerElementCaching.class);
        mockManager3 = mock(AbstractManagerElementCaching.class);

        uuid1 = UUID.randomUUID();
        mockVal1 = new Value(uuid1);
        uuid2 = UUID.randomUUID();
        mockVal2 = new Value2(uuid2);
        uuid3 = UUID.randomUUID();
        mockVal3 = new Value3(uuid3);

        optVal1 = Optional.of(new WeakReference<>(mockVal1));
        optVal2 = Optional.of(new WeakReference<>(mockVal2));
        optVal3 = Optional.of(new WeakReference<>(mockVal3));

        when(mockManager1.get(any(UUID.class))).then((invocation -> {
            UUID uuid = (UUID) invocation.getArguments()[0];
            if (uuid.equals(mockVal1.getUuid())) {
                return Optional.of(new WeakReference<>(mockVal1));
            } else {
                return Optional.empty();
            }
        }));
        when(mockManager1.search(any(Predicate.class))).then(invocation -> {
            Predicate predicate = (Predicate) invocation.getArguments()[0];
            List l = new ArrayList();
            if (predicate.test(mockVal1))
                l.add(mockVal1);

            return l;
        });

        doAnswer(invocation -> {
            Consumer consumer = (Consumer) invocation.getArguments()[0];
            consumer.accept(mockVal1);
            return null;
        }).when(mockManager1).forEach(any());

        when(mockManager2.get(any(UUID.class))).then((invocation -> {
            UUID uuid = (UUID) invocation.getArguments()[0];

            if (uuid.equals(mockVal2.getUuid())) {
                return Optional.of(new WeakReference<>(mockVal2));
            } else {
                return Optional.empty();
            }
        }));
        when(mockManager2.search(any(Predicate.class))).then(invocation -> {
            Predicate predicate = (Predicate) invocation.getArguments()[0];
            List l = new ArrayList();
            if (predicate.test(mockVal2))
                l.add(mockVal2);

            return l;
        });

        doAnswer(invocation -> {
            Consumer consumer = (Consumer) invocation.getArguments()[0];
            consumer.accept(mockVal2);
            return null;
        }).when(mockManager2).forEach(any());

        when(mockManager3.get(any(UUID.class))).then((invocation -> {
            UUID uuid = (UUID) invocation.getArguments()[0];

            if (uuid.equals(mockVal3.getUuid())) {
                return Optional.of(new WeakReference<>(mockVal3));
            } else {
                return Optional.empty();
            }
        }));
        when(mockManager3.search(any(Predicate.class))).then(invocation -> {
            Predicate predicate = (Predicate) invocation.getArguments()[0];
            List l = new ArrayList();
            if (predicate.test(mockVal3))
                l.add(mockVal3);

            return l;
        });

        doAnswer(invocation -> {
            Consumer consumer = (Consumer) invocation.getArguments()[0];
            consumer.accept(mockVal3);
            return null;
        }).when(mockManager3).forEach(any());
    }

    @Test
    public void constructor() {
        IInstanceProvider<Interface1> provider = new SimpleInstanceProvider<>(Interface1.class,
                mockManager1, null, null);

        assertEquals(1, Array.getLength(Whitebox.getInternalState(provider, "managers")));
        assertEquals(mockManager1, Array.get(Whitebox.getInternalState(provider, "managers"), 0));
    }

    @Test
    public void constructor2() {
        IInstanceProvider<Interface1> provider = new SimpleInstanceProvider<>(Interface1.class,
                mockManager1, mockManager1, mockManager2);

        assertEquals(2, Array.getLength(Whitebox.getInternalState(provider, "managers")));
    }

    @Test
    public void get() {
        IInstanceProvider<Interface1> provider = new SimpleInstanceProvider<>(Interface1.class,
                mockManager1, mockManager2, mockManager3);

        assertNotNull(provider.get(mockVal1.getUuid()));
        assertNull(provider.get(mockVal2.getUuid()));
        assertNull(provider.get(mockVal3.getUuid()));
    }

    @Test
    public void get2() {
        IInstanceProvider<Interface2> provider = new SimpleInstanceProvider<>(Interface2.class,
                mockManager1, mockManager2, mockManager3);

        assertNotNull(provider.get(mockVal1.getUuid()));
        assertNotNull(provider.get(mockVal2.getUuid()));
        assertNull(provider.get(mockVal3.getUuid()));
    }

    @Test
    public void get3() {
        IInstanceProvider<Interface3> provider = new SimpleInstanceProvider<>(Interface3.class,
                mockManager1, mockManager2, mockManager3);

        assertNotNull(provider.get(mockVal1.getUuid()));
        assertNotNull(provider.get(mockVal2.getUuid()));
        assertNotNull(provider.get(mockVal3.getUuid()));
    }

    @Test
    public void forEachHolder() {
        IInstanceProvider<Interface3> provider = new SimpleInstanceProvider<>(Interface3.class,
                mockManager1, mockManager2, mockManager3);

        List<Interface3> values = new ArrayList<>();
        provider.forEachHolder(values::add);
        assertEquals(3, values.size());
    }

    @Test
    public void search() {
        IInstanceProvider<Interface3> provider = new SimpleInstanceProvider<>(Interface3.class,
                mockManager1, mockManager2, mockManager3);

        assertEquals(1, provider.search(val -> uuid1.equals(val.getUuid())).size());
        assertEquals(1, provider.search(val -> uuid2.equals(val.getUuid())).size());
        assertEquals(1, provider.search(val -> uuid3.equals(val.getUuid())).size());
    }

    private interface InterfaceAll extends IPluginObject {

    }

    private interface Interface1 extends InterfaceAll {

    }

    private interface Interface2 extends InterfaceAll {

    }

    private interface Interface3 extends InterfaceAll {

    }

    private class Value extends CachedElement<UUID> implements Interface1, Interface2, Interface3 {
        private Value() {
            super(null);
        }

        public Value(UUID key) {
            super(key);
        }

        @Override
        public UUID getUuid() {
            return getKey();
        }
    }

    private class Value2 extends CachedElement<UUID> implements Interface2, Interface3 {
        private Value2() {
            super(null);
        }

        public Value2(UUID key) {
            super(key);
        }

        @Override
        public UUID getUuid() {
            return getKey();
        }
    }

    private class Value3 extends CachedElement<UUID> implements Interface3 {
        private Value3() {
            super(null);
        }

        public Value3(UUID key) {
            super(key);
        }

        @Override
        public UUID getUuid() {
            return getKey();
        }
    }
}