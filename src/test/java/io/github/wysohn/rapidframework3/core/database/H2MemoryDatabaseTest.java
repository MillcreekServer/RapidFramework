package io.github.wysohn.rapidframework3.core.database;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class H2MemoryDatabaseTest {

    @Test
    public void test() throws Exception{
        H2MemoryDatabase<UUID, Employee> db = new H2MemoryDatabase<>(Employee.class,
                                                               "Employee");

        UUID uuid1 = UUID.randomUUID();
        Employee employee1 = new Employee(uuid1);
        employee1.name = "David";
        db.save(uuid1, employee1);

        Employee loaded = db.load(uuid1);

        assertEquals(uuid1, loaded.getKey());
        assertEquals("David", loaded.name);
    }

}