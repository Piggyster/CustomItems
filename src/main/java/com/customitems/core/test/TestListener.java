package com.customitems.core.test;

import com.customitems.core.item.Item;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TestListener implements Listener {

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(stack.getType().isAir()) return;

        Item item = Item.of(stack);
        if(false) {
           // PouchProperty property = item.getProperty(PouchProperty.TYPE);
            //Inventory inventory = property.getInventory();
            //event.getPlayer().openInventory(inventory);

            String nbtString = NBT.get(stack, nbt -> {
                return nbt.toString();
            });
            Bukkit.getLogger().severe(nbtString);
        }
    }

    @EventHandler
    public void onEvent(InventoryCloseEvent event) {
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(stack.getType().isAir()) return;

        Item item = Item.of(stack);
        if(false) {
            //PouchProperty property = item.getProperty(PouchProperty.TYPE);
            //Inventory inventory = property.getInventory();
            //if(inventory.equals(event.getInventory())) {
            //    property.updateContents();
                //item.save();
                item.updateDisplay();

        }
    }
}
