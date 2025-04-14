package com.customitems.core.item;

import org.bukkit.ChatColor;

public enum ItemRarity {

    COMMON("Common", ChatColor.WHITE),
    UNCOMMON("Uncommon", ChatColor.GREEN),
    RARE("Rare", ChatColor.BLUE),
    EPIC("Epic", ChatColor.DARK_PURPLE),
    LEGENDARY("Legendary", ChatColor.GOLD);

    private final String display;
    private final ChatColor color;

    ItemRarity(String display, ChatColor color) {
        this.display = display;
        this.color = color;
    }


    public String getDisplay() {
        return display;
    }

    public ChatColor getColor() {
        return color;
    }
}
