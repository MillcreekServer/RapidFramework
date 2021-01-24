package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework3.core.caching.CachedElement;
import org.junit.Test;

import javax.persistence.*;
import java.util.UUID;

public class H2MemoryDatabaseTest {

    @Entity
    @Table
    private static class Employee extends CachedElement<UUID> {
        @Id
        @GeneratedValue
        @Column
        private int id;

        @Column
        private String name;

        @Deprecated
        public Employee() {
            super(null);
        }

        public Employee(UUID key) {
            super(key);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void test() {
        throw new RuntimeException();
    }
}