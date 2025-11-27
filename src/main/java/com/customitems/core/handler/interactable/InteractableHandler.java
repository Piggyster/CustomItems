package com.customitems.core.handler.interactable;

import com.customitems.core.item.Item;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class InteractableHandler {

    private final BiConsumer<Player, Item> leftClickHandler;
    private final BiConsumer<Player, Item> rightClickHandler;

    public InteractableHandler(BiConsumer<Player, Item> leftClickHandler, BiConsumer<Player, Item> rightClickHandler) {
        this.leftClickHandler = leftClickHandler;
        this.rightClickHandler = rightClickHandler;
    }

    public void onLeftClick(Player player, Item item) {
        if(leftClickHandler == null) return;
        leftClickHandler.accept(player, item);
    }

    public void onRightClick(Player player, Item item) {
        if(rightClickHandler == null) return;
        rightClickHandler.accept(player, item);
    }
}
