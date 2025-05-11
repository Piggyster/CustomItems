package com.customitems.core.stat;

import org.bukkit.ChatColor;

public enum StatType {
    DAMAGE("Damage", ChatColor.RED),
    SHIELD("Shield", ChatColor.AQUA),
    SPEED("Speed", ChatColor.YELLOW);

    private final String displayName;
    private final ChatColor color;


    StatType(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }
}
