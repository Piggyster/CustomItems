package com.customitems.core.handler.interactable;

import com.customitems.core.item.Item;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InteractableHandler {

    private final Consumer<InteractionContext> leftClickHandler;
    private final Consumer<InteractionContext> rightClickHandler;

    public InteractableHandler(Consumer<InteractionContext> leftClickHandler, Consumer<InteractionContext> rightClickHandler) {
        this.leftClickHandler = leftClickHandler;
        this.rightClickHandler = rightClickHandler;
    }

    public void onLeftClick(InteractionContext context) {
        if(leftClickHandler == null) return;
        leftClickHandler.accept(context);
    }

    public void onRightClick(InteractionContext context) {
        if(rightClickHandler == null) return;
        rightClickHandler.accept(context);
    }
}
