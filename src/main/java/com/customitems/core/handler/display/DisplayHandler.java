package com.customitems.core.handler.display;

import org.bukkit.entity.Player;

public interface DisplayHandler {

    void processDisplay(Player player, DisplayVisitor visitor);
}
