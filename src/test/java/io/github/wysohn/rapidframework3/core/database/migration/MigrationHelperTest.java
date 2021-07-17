package io.github.wysohn.rapidframework3.core.database.migration;

import io.github.wysohn.rapidframework4.core.caching.CachedElement;
import io.github.wysohn.rapidframework4.core.database.Database;
import io.github.wysohn.rapidframework4.core.database.migration.MigrationHelper;
import io.github.wysohn.rapidframework4.core.database.migration.MigrationSteps;
import io.github.wysohn.rapidframework4.utils.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

public class MigrationHelperTest {

    @Test
    public void testStart() throws Exception{
        List<Pair<UUID, Data1>> fromDatabase = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            UUID uuid = UUID.randomUUID();
            Data1 data = new Data1(uuid);
            data.data = i;
            fromDatabase.add(Pair.of(uuid, data));
        }

        Logger logger = mock(Logger.class);
        Database<UUID, Data1> from = mock(Database.class);
        Database<UUID, Data2> to = mock(Database.class);

        when(from.getKeys()).thenReturn(fromDatabase.stream()
                                                .map(pair -> pair.key)
                                                .collect(Collectors.toSet()));
        when(from.load(any())).then(invocation -> {
            UUID key = (UUID) invocation.getArguments()[0];
            for (Pair<UUID, Data1> pair : fromDatabase) {
                if(pair.key.equals(key))
                    return pair.value;
            }

            throw new RuntimeException();
        });

        MigrationHelper<UUID, Data1, Data2> helper
                = new MigrationHelper<>(logger,
                                        from,
                                        to,
                                        UUID::fromString,
                                        Data2::new,
                                        MigrationSteps.Builder.<UUID, Data1, Data2>begin()
                                                .step((a, b) -> b.data = String.valueOf(a.data))
                                                .build());

        helper.start();
        helper.waitForTermination(5, TimeUnit.SECONDS);

        for (int i = 0; i < 4; i++) {
            UUID key = fromDatabase.get(i).key;
            verify(to).save(key, new Data2(key, String.valueOf(i)));
        }
    }

    public static class Data1 extends CachedElement<UUID> {
        private int data;
        public Data1(UUID key) {
            super(key);
        }

        public Data1(Data1 copy) {
            super((UUID) null);
            this.data = copy.data;
        }
    }

    public static class Data2 extends CachedElement<UUID>{
        private String data;
        public Data2(UUID key) {
            super(key);
        }

        public Data2(UUID key, String data) {
            super(key);
            this.data = data;
        }

        public Data2(Data2 copy) {
            super((UUID) null);
            this.data = copy.data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Data2 data2 = (Data2) o;
            return Objects.equals(data, data2.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data);
        }
    }
}