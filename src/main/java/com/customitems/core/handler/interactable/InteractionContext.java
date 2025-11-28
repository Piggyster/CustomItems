package com.customitems.core.handler.interactable;

import com.customitems.core.item.Item;
import org.bukkit.entity.Player;

public record InteractionContext(Player player, Item item, int slot) {
}
