package com.customitems.core.handler;

import com.customitems.core.item.Item;
import org.bukkit.entity.Player;

import java.util.List;

public interface LoreHandler {

    List<String> contributeLore(Item item, Player player);
}
