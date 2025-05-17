package com.customitems.core.property.impl;

import com.customitems.core.property.*;
import com.customitems.core.stat.StatPhase;
import com.customitems.core.stat.StatProvider;
import com.customitems.core.stat.StatType;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class StatProperty extends AbstractProperty implements LoreContributor, StatProvider {

    public static final PropertyType<StatProperty> TYPE = PropertyType.of(StatProperty.class, "stats")
            .json(json -> {
                Map<StatType, Double> stats = new HashMap<>();
                JsonObject jsonObject = json.getAsJsonObject();
                for(String statType : jsonObject.keySet()) {
                    StatType type = StatType.valueOf(statType.toUpperCase());
                    double value = jsonObject.get(statType).getAsDouble();
                    //double value = json.getAsJsonObject(statType).getAsDouble();
                    stats.put(type, value);
                }
                return new StatProperty(stats);
            }).build();

    private final Map<StatType, Double> stats;

    public StatProperty(Map<StatType, Double> stats) {
        this.stats = stats;
    }

    public double getStat(StatType type) {
        return stats.getOrDefault(type, 0.0);
    }

    /*
    public void setStat(StatType type, double value) {
        stats.put(type, value);
    }
    */

    public Map<StatType, Double> getStats() {
        return ImmutableMap.copyOf(stats);
    }

    @Override
    public PropertyType<StatProperty> getType() {
        return TYPE;
    }

    @Override
    public int getLorePriority() {
        return 100;
    }

    @Override
    public void contributeLore(LoreVisitor visitor) {
        for(Map.Entry<StatType, Double> entry : stats.entrySet()) {
            StatType stat = entry.getKey();
            double value = entry.getValue();
            visitor.visit(formatStatLine(stat, value));
        }
    }
    //TODO figure out a way to track if a value is effective or modified.

    private String formatStatLine(StatType type, double value) {
        return "&e" + type + ": &f" + value;
    }

    @Override
    public void applyStats(Map<StatType, Double> stats) {
        getStats().forEach((stat, value) -> stats.merge(stat, value, Double::sum));
    }

    @Override
    public StatPhase getPhase() {
        return StatPhase.BASE;
    }

    @Override
    public String toString() {
        return "StatProperty{" +
                "stats=" + stats +
                '}';
    }
}
