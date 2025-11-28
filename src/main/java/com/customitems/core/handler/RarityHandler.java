package com.customitems.core.handler;

import com.customitems.core.item.Item;
import com.customitems.core.item.ItemRarity;
import org.bukkit.entity.Player;

public interface RarityHandler {
    ItemRarity processRarity(Item item, Player player);
}
