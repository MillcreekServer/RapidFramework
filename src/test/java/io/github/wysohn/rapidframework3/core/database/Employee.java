package io.github.wysohn.rapidframework3.core.database;

import io.github.wysohn.rapidframework4.core.caching.CachedElement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table
class Employee extends CachedElement<UUID> {
    @Column
    String name;

    @Deprecated
    public Employee() {
        super((UUID) null);
    }

    private Employee(Employee copy) {
        super(copy.getKey());
        name = copy.name;
    }

    public Employee(UUID key) {
        super(key);
    }

    public String getName() {
        return read(() -> name);
    }

    public void setName(String name) {
        mutate(() -> this.name = name);
    }
}
