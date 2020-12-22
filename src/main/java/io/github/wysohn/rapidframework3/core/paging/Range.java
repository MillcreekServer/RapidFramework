package io.github.wysohn.rapidframework3.core.paging;

import io.github.wysohn.rapidframework3.utils.Validation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Range implements Comparable<Range> {
    public final int index;
    public final int size;

    private Range(int index, int size) {
        Validation.validate(index, val -> val >= 0, "index cannot be negative");
        Validation.validate(size, val -> val >= 0, "size cannot be negative");
        this.index = index;
        this.size = size;
    }

    public static Range of(int index, int size) {
        return new Range(index, size);
    }

    @Override
    public int compareTo(@NotNull Range o) {
        return Integer.compare(index, o.index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return index == range.index &&
                size == range.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, size);
    }
}
