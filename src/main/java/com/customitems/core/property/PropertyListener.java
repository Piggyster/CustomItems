package com.customitems.core.property;

import com.customitems.core.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class PropertyListener implements Listener {

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        if(!event.hasItem()) return;
        Item item = Item.of(event.getItem());
        if(item == null || item.getTemplate().isVanilla()) return;
        item.handleEvent(event);
    }

    @EventHandler
    public void onEvent(PlayerToggleSneakEvent event) {
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(stack.getType().isAir()) return;
        Item item = Item.of(stack);
        if(item == null || item.getTemplate().isVanilla()) return;
        item.handleEvent(event);
    }

    @EventHandler
    public void onEvent(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(stack.getType().isAir()) return;
        Item item = Item.of(stack);
        if(item == null || item.getTemplate().isVanilla()) return;
        item.handleEvent(event);
    }
}
