package com.customitems.core.property.impl;

import com.customitems.core.property.*;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class StatProperty extends AbstractProperty implements LoreContributor, ModifiableProperty<StatModification> {

    private final Map<StatType, Integer> stats;
    private final Map<StatType, StatModification> modifications;

    public StatProperty(Map<StatType, Integer> stats) {
        this.stats = stats;
        modifications = new HashMap<>();
    }

    public int getStat(StatType type) {
        return stats.getOrDefault(type, 0);
    }

    public void setStat(StatType type, int value) {
        stats.put(type, value);
    }

    public Map<StatType, Integer> getStats() {
        return ImmutableMap.copyOf(stats);
    }

    @Override
    public String getType() {
        return "stats";
    }

    @Override
    public PropertyPriority getPriority() {
        return PropertyPriority.MASTER;
    }

    @Override
    public int getLorePriority() {
        return 100;
    }

    @Override
    public void contributeLore(LoreVisitor visitor) {
        for(StatType type : stats.keySet()) {
            visitor.visit(formatStatLine(type));
        }
    }
    //TODO figure out a way to track if a value is effective or modified.

    private String formatStatLine(StatType type) {
        return "";
    }

    @Override
    public void modify(StatModification mod) {
        modifications.put(mod.type(), mod);
        //TODO operations functions
    }

}
