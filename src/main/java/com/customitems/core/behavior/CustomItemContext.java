package com.customitems.core.behavior;

import com.customitems.core.item.CustomItem;
import org.bukkit.entity.Player;

public record CustomItemContext(CustomItem item, Player player) {

}
