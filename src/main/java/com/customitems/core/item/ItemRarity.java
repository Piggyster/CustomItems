package com.customitems.core.item;

import org.bukkit.ChatColor;

public enum ItemRarity {
    COMMON("Common", ChatColor.WHITE),
    RARE("Rare", ChatColor.BLUE),
    LEGENDARY("Legendary", ChatColor.GOLD);

    private final String displayName;
    private final ChatColor color;

    ItemRarity(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public static ItemRarity fromString(String rarity) {
        for (ItemRarity itemRarity : values()) {
            if (itemRarity.name().equalsIgnoreCase(rarity)) {
                return itemRarity;
            }
        }
        return null;
    }
}
