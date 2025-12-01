package com.customitems.core.stat;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public class ItemStatistics implements Iterable<StatType> {

    private Map<StatType, Float> values;

    public ItemStatistics(Map<StatType, Float> values) {
        this.values = values;
    }

    public float getValue(StatType stat) {
        return values.getOrDefault(stat, 0.0f);
    }

    public Map<StatType, Float> getValues() {
        return ImmutableMap.copyOf(values);
    }

    @NotNull
    @Override
    public Iterator<StatType> iterator() {
        return values.keySet().iterator();
    }
}
