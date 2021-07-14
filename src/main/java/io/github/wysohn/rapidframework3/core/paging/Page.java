package io.github.wysohn.rapidframework3.core.paging;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {
    private final Range range;
    private List<T> list = new ArrayList<>();
    private long lastUpdate = -1L;

    public Page(Range range) {
        this.range = range;
    }

    public Range getRange() {
        return range;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = new ArrayList<>(list);
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
