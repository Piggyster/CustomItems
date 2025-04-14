package com.customitems.core.inventory;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CustomPlayerInventory {

    private Map<Integer, CustomItem> contents;
    private Player player;
    private PlayerInventory orgin;

    public CustomPlayerInventory(Player player) {
        contents = new ConcurrentHashMap<>();
        this.player = player;
        orgin = player.getInventory();

        refreshInventory();
    }

    public void refreshInventory() {
        contents.clear();

        ItemStack[] items = orgin.getContents();
        for(int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item != null && !item.getType().isAir()) {
                Optional<CustomItem> customItem = CustomItemsPlugin.getInstance()
                        .getItemManager().getCustomItem(item);
                if(customItem.isPresent()) {
                    customItem.get().savePropertiesToItem();
                    contents.put(i, customItem.get());
                }
            }
        }
    }

    public void refreshSlot(int slot) {
        if(slot < 0 || slot >= orgin.getSize()) {
            return;
        }

        ItemStack item = orgin.getItem(slot);
        if(item == null || item.getType().isAir()) {
            contents.remove(slot);
        } else {
            Optional<CustomItem> customItem = CustomItemsPlugin.getInstance().getItemManager().getCustomItem(item);

            if(customItem.isPresent()) {
                contents.put(slot, customItem.get());
            } else {
                contents.remove(slot);
            }
        }
    }

    public Optional<CustomItem> getItem(int slot) {
        if(slot < 0 || slot >= orgin.getSize()) {
            return Optional.empty();
        }

        ItemStack item = orgin.getItem(slot);
        if(item != null && !item.getType().isAir()) {
            CustomItem cachedItem = contents.get(slot);
            if(cachedItem != null && cachedItem.getItemStack().equals(item)) {
                return Optional.of(cachedItem);
            }

            Optional<CustomItem> customItem = CustomItemsPlugin.getInstance().getItemManager().getCustomItem(item);

            if(customItem.isPresent()) {
                contents.put(slot, customItem.get());
                return customItem;
            }
        }
        contents.remove(slot);
        return Optional.empty();
    }

    public void setItem(int slot, CustomItem customItem) {
        if(customItem == null) {
            orgin.setItem(slot, null);
            contents.remove(slot);
        } else {
            customItem.savePropertiesToItem();
            orgin.setItem(slot, customItem.getItemStack());
            contents.put(slot, customItem);
        }
    }

    public boolean addItem(CustomItem customItem) {
        for(int i = 0; i < 36; i++) {
            if(orgin.getItem(i) == null || orgin.getItem(i).getType().isAir()) {
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
}
