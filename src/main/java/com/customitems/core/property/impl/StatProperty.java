package com.customitems.core.property.impl;

import com.customitems.core.property.AbstractProperty;
import com.customitems.core.property.LoreContributor;
import com.customitems.core.stat.StatType;
import com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatProperty extends AbstractProperty implements LoreContributor {

    private final Map<StatType, Integer> stats;

    public StatProperty(Map<StatType, Integer> stats) {
        super();
        this.stats = stats;
    }

    public int getValue(StatType type) {
        return stats.getOrDefault(type, 0);
    }

    public Map<StatType, Integer> getStats() {
        return ImmutableMap.copyOf(stats);
    }


    @Override
    public int getLorePriority() {
        return 100;
    }

    @Override
    public List<String> contributeLore() {
        List<String> lore = new ArrayList<>();
        stats.forEach((stat, value) -> {
            lore.add(stat.getColor() + stat.getDisplayName() + ChatColor.WHITE + ": " + value);
        });
        return lore;
    }

    @Override
    public String getType() {
        return "stats";
    }
}
