package com.customitems.core.inventory;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryManager implements Listener {

    private final CustomItemsPlugin plugin;
    private Map<UUID, CustomPlayerInventory> inventories;

    public InventoryManager() {
        inventories = new ConcurrentHashMap<>();
        plugin = CustomItemsPlugin.getInstance();

        Bukkit.getPluginManager().registerEvents(this, plugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            inventories.put(player.getUniqueId(), new CustomPlayerInventory(player));
        }

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            inventories.forEach((player, inventory) -> inventory.refreshInventory());
        }, 0L, 100L);
    }

    public CustomPlayerInventory getPlayerInventory(Player player) {
        return inventories.computeIfAbsent(player.getUniqueId(), uuid -> new CustomPlayerInventory(player));
    }

    private <T extends Event> void handleEvent(Player player, T event) {
        CustomPlayerInventory inventory = getPlayerInventory(player);
        inventory.handleEvent(event);

        Bukkit.getLogger().warning("Handling event: " + event.getEventName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        inventories.put(player.getUniqueId(), new CustomPlayerInventory(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        inventories.remove(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        handleEvent(player, event);

    }


    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;

        Item droppedItem = event.getItem();

        CustomItem item = CustomItemsPlugin.getInstance().getItemManager().getCustomItem(droppedItem.getItemStack());

        if(item != null) {
            item.savePropertiesToItem();
            droppedItem.setItemStack(item.getItemStack());
        } else {
            droppedItem.setItemStack(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        handleEvent(event.getPlayer(), event);
    }
}
