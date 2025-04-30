package com.customitems.core.inventory;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomPlayerInventory {

    private Map<Integer, CustomItem> contents;
    private Player player;
    private PlayerInventory origin;
    private Set<Integer> listeners;

    public CustomPlayerInventory(Player player) {
        contents = new ConcurrentHashMap<>();
        listeners = new HashSet<>();
        this.player = player;
        origin = player.getInventory();
        refreshInventory();
    }

    public void refreshInventory() {
        contents.clear();
        listeners.clear();

        ItemStack[] items = origin.getContents();
        for(int i = 0; i < items.length; i++) {
            refreshSlot(i);
        }
    }

    public CustomItem refreshSlot(int slot) {
        if(slot < 0 || slot >= origin.getSize()) {
            return null;
        }
        ItemStack item = origin.getItem(slot);
        if(item == null || item.getType().isAir()) {
            contents.remove(slot);
            listeners.remove(slot);
            return null;
        } else {
            CustomItem customItem = CustomItemsPlugin.getInstance().getItemManager().getCustomItem(item);
            if(customItem != null) {
                contents.put(slot, customItem);
                if(customItem.hasEventHandler()) {
                    listeners.add(slot);
                } else {
                    listeners.remove(slot);
                }
                return customItem;
            } else {
                contents.remove(slot);
                listeners.remove(slot);
                return null;
            }
        }
    }

    public CustomItem getItem(int slot) {
        if(slot < 0 || slot >= origin.getSize()) {
            return null;
        }

        ItemStack item = origin.getItem(slot);
        if(item != null && !item.getType().isAir()) {
            CustomItem cachedItem = contents.get(slot);
            if(cachedItem != null && cachedItem.getItemStack().equals(item)) {
                return cachedItem;
            }
            return refreshSlot(slot);
        }
        contents.remove(slot);
        listeners.remove(slot);
        return null;
    }

    public void setItem(int slot, CustomItem customItem) {
        if(customItem == null) {
            origin.setItem(slot, null);
            contents.remove(slot);
            listeners.remove(slot);
        } else {
            customItem.savePropertiesToItem();
            origin.setItem(slot, customItem.getItemStack());
            contents.put(slot, customItem);

            if(customItem.hasEventHandler()) {
                listeners.add(slot);
            } else {
                listeners.remove(slot);
            }
        }
    }

    public boolean addItem(CustomItem customItem) {
        for(int i = 0; i < 36; i++) {
            if(origin.getItem(i) == null || origin.getItem(i).getType().isAir()) {
                setItem(i, customItem);
                return true;
            }
        }
        return false;
    }

    public Map<Integer, CustomItem> getContents() {
        refreshInventory();
        return contents;
    }


    public <T extends Event> void handleEvent(T event) {
        for(int slot : listeners) {
            CustomItem item = contents.get(slot);
            if(item == null) {
                throw new IllegalStateException("Listener is present but CustomItem is missing at slot " + slot);
            }
            item.handleEvent(event);
        }
    }
}
