package com.customitems.core.handler.interactable;

import com.customitems.core.item.Item;
import org.bukkit.entity.Player;

public class InteractionContext {

    private final Player player;
    private final Item item;
    private final int slot;
    private boolean eventCancelled = false;

    public InteractionContext(Player player, Item item, int slot) {
        this.player = player;
        this.item = item;
        this.slot = slot;
    }

    public Player player() {
        return player;
    }

    public Item item() {
        return item;
    }

    public int slot() {
        return slot;
    }

    public boolean eventCancelled() {
        return eventCancelled;
    }

    public void setEventCancelled(boolean eventCancelled) {
        this.eventCancelled = eventCancelled;
    }
}
