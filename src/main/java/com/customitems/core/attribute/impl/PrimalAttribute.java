package com.customitems.core.attribute.impl;

import com.customitems.core.attribute.Attribute;
import com.customitems.core.handler.RarityHandler;
import com.customitems.core.handler.display.DisplayHandler;
import com.customitems.core.handler.display.DisplayVisitor;
import com.customitems.core.item.Item;
import com.customitems.core.item.ItemRarity;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrimalAttribute extends Attribute<Void> implements RarityHandler, DisplayHandler {
    @Override
    public String getKey() {
        return "primal";
    }

    @Override
    public Void loadFromNBT(ReadableNBT nbt) {
        return null;
    }

    @Override
    public void saveToNBT(ReadWriteNBT nbt) {
        nbt.setByteArray(getKey(), new byte[0]);
    }

    @Override
    public ItemRarity processRarity(Item item, Player player) {
        return ItemRarity.SPECIAL;
    }

    @Override
    public void processDisplay(Player player, DisplayVisitor visitor) {
        String color = ChatColor.getLastColors(visitor.getDisplayName());
        visitor.setDisplayName(color + "Primal " + visitor.getDisplayName());
    }
}
