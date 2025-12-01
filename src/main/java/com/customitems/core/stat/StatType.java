package com.customitems.core.stat;

import org.bukkit.ChatColor;

public enum StatType {
    HEALTH("Health", ChatColor.RED, 100f),
    DAMAGE("Damage", ChatColor.BLUE, 1.0f),
    MINING_SPEED("Mining Speed", ChatColor.YELLOW, 0.0f),
    DEFENCE("Defence", ChatColor.GREEN, 0.0f),
    MANA("Mana", ChatColor.AQUA, 50.0f);

    private final String displayName;
    private final ChatColor color;
    private final float baseValue;

    StatType(String displayName, ChatColor color, float baseValue) {
        this.displayName = displayName;
        this.color = color;
        this.baseValue = baseValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public float getBaseValue() {
        return baseValue;
    }
}