package com.customitems.core.component.impl;

import com.customitems.core.component.Component;
import com.customitems.core.handler.interactable.InteractableHandler;
import com.customitems.core.handler.interactable.InteractableRegistry;
import com.customitems.core.item.Item;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class InteractableComponent extends Component {

    public static final String KEY = "interactable";

    public static InteractableComponent deserialize(JsonElement json) {
        return new InteractableComponent(json.getAsString());
    }

    private final InteractableHandler handler;

    public InteractableComponent(BiConsumer<Player, Item> leftClickHandler, BiConsumer<Player, Item> rightClickHandler) {
        handler = new InteractableHandler(leftClickHandler, rightClickHandler);
    }

    public InteractableComponent(String handlerId) {
        handler = InteractableRegistry.getHandler(handlerId);
        if(handler == null) {
            throw new IllegalStateException("InteractableComponent handler cannot be null!");
        }
    }

    public void onLeftClick(Player player, Item item) {
        handler.onLeftClick(player, item);
    }

    public void onRightClick(Player player, Item item) {
        handler.onRightClick(player, item);
    }
}
