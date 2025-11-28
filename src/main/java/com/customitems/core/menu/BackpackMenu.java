package com.customitems.core.menu;

import com.customitems.core.attribute.impl.BackpackDataAttribute;
import com.customitems.core.handler.interactable.InteractionContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class BackpackMenu implements InventoryHolder {

    private Inventory inventory;
    private Player player;

    private int size;
    private BackpackDataAttribute.BackpackData backpackData;
    private InteractionContext interactionContext;

    public BackpackMenu(int size, BackpackDataAttribute.BackpackData backpackData, InteractionContext interactionContext) {
        this.size = size;
        this.backpackData = backpackData;
        this.interactionContext = interactionContext;
    }

    public void open(Player player) {
        this.player = player;
        createInventory();
        player.openInventory(inventory);
    }

    public void createInventory() {
        inventory = Bukkit.createInventory(this, size * 9, "Backpack"); //size in title

        backpackData.getContents().forEach((slot, item) -> {
            inventory.setItem(slot, item.update(player, item.getStack()));
        });
    }

    public InteractionContext getInteractionContext() {
        return interactionContext;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
