package com.customitems.core.menu;

import com.customitems.core.attribute.Attribute;
import com.customitems.core.attribute.impl.BackpackDataAttribute;
import com.customitems.core.component.impl.BackpackComponent;
import com.customitems.core.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InventoryListener implements Listener {

    @EventHandler
    public void onEvent(InventoryClickEvent event) {
        if(!(event.getInventory().getHolder() instanceof BackpackMenu menu)) return;
        Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == null) return;
        int backpackSlot = menu.getInteractionContext().slot();

        if(event.getClickedInventory().getType() == InventoryType.PLAYER && event.getSlot() == backpackSlot) {
            event.setCancelled(true);
            return;
        }

        /*ItemStack[] stacks = event.getInventory().getContents();
        Map<Integer, Item> items = new HashMap<>();

        for(int i = 0; i < stacks.length; i++) {
            if(stacks[i] == null || stacks[i].getType() == Material.AIR) continue;
            items.put(i, Item.of(stacks[i]));
        }

        BackpackDataAttribute.BackpackData data = new BackpackDataAttribute.BackpackData(items);

        ItemStack backpackStack = player.getInventory().getItem(backpackSlot);
        assert(backpackStack != null);
        Item backpack = Item.of(backpackStack);

        if(items.isEmpty()) {
            backpack.removeAttribute(BackpackDataAttribute.class);
        } else if(backpack.hasAttribute(BackpackDataAttribute.class)) {
            Attribute<BackpackDataAttribute.BackpackData> attribute = backpack.getAttribute(BackpackDataAttribute.class);
            attribute.setValue(data);
        } else {
            BackpackDataAttribute attribute = new BackpackDataAttribute();
            attribute.setValue(data);
            backpack.addAttribute(attribute);
        }

        backpack.update(player, backpackStack);*/
    }

    @EventHandler
    public void onEvent(InventoryCloseEvent event) {
        if(!(event.getInventory().getHolder() instanceof BackpackMenu menu)) return;
        Player player = (Player) event.getPlayer();

        int backpackSlot = menu.getInteractionContext().slot();

        ItemStack[] stacks = event.getInventory().getContents();
        Map<Integer, Item> items = new HashMap<>();

        for(int i = 0; i < stacks.length; i++) {
            if(stacks[i] == null || stacks[i].getType() == Material.AIR) continue;
            items.put(i, Item.of(stacks[i]));
        }

        BackpackDataAttribute.BackpackData data = new BackpackDataAttribute.BackpackData(items);

        ItemStack backpackStack = player.getInventory().getItem(backpackSlot);
        assert(backpackStack != null);
        Item backpack = Item.of(backpackStack);
        assert(backpack.hasComponent(BackpackComponent.class));

        if(items.isEmpty()) {
            backpack.removeAttribute(BackpackDataAttribute.class);
        } else if(backpack.hasAttribute(BackpackDataAttribute.class)) {
            Attribute<BackpackDataAttribute.BackpackData> attribute = backpack.getAttribute(BackpackDataAttribute.class);
            attribute.setValue(data);
        } else {
            BackpackDataAttribute attribute = new BackpackDataAttribute();
            attribute.setValue(data);
            backpack.addAttribute(attribute);
        }

        backpack.update(player, backpackStack);
    }
}
