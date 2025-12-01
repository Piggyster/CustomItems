package com.customitems.core.stat;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class StatListener implements Listener {

    private static final int[] EQUIPMENT_SLOTS = {36, 37, 38, 39};

    private final StatStorage storage;

    public StatListener(StatStorage storage) {
        this.storage = storage;
    }

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        storage.recalculate(event.getPlayer());
    }

    @EventHandler
    public void onEvent(PlayerSwapHandItemsEvent event) {
        storage.recalculate(event.getPlayer());
    }

    @EventHandler
    public void onEvent(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;

        if(isEquipmentSlot(event.getRawSlot()) || event.getRawSlot() == player.getInventory().getHeldItemSlot()) {
            storage.recalculate(player);
        }
    }

    @EventHandler
    public void onEvent(InventoryDragEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;

        boolean match = event.getRawSlots().stream().anyMatch(this::isEquipmentSlot);
        if(match) {
            storage.recalculate(player);
        }
    }

    @EventHandler
    public void onEvent(PlayerItemHeldEvent event) {
        Bukkit.getScheduler().runTaskLater(ItemPlugin.get(), () -> storage.recalculate(event.getPlayer()), 1L);
        //storage.recalculate(event.getPlayer());
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack stack = event.getItem();
        if(stack == null || stack.getType().isAir()) return;

        if(isArmor(stack.getType())) {
            EquipmentSlot slot = getArmorSlot(stack.getType());
            if(slot == null) return;
            ItemStack armorStack = event.getPlayer().getInventory().getItem(slot);
            if(armorStack == null || !armorStack.isSimilar(stack)) {
                storage.recalculate(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEvent(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        org.bukkit.entity.Item itemEntity = event.getItem();
        Item item = Item.of(itemEntity.getItemStack());
        item.update(player, item.getStack());
        itemEntity.setItemStack(item.getStack());
        storage.recalculate(player);
    }

    @EventHandler
    public void onEvent(EntityDropItemEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        storage.recalculate(player);
    }

    private boolean isArmor(Material material) {
        String materialName = material.toString();
        return materialName.endsWith("_HELMET") || materialName.endsWith("_CHESTPLATE") ||
                materialName.endsWith("_LEGGINGS") || materialName.endsWith("_BOOTS");
    }

    private EquipmentSlot getArmorSlot(Material armor) {
        String type = armor.name().split("_")[1];
        return switch(type) {
            case "HELMET" -> EquipmentSlot.HEAD;
            case "CHESTPLATE" -> EquipmentSlot.CHEST;
            case "LEGGINGS" -> EquipmentSlot.LEGS;
            case "BOOTS" -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    private boolean isEquipmentSlot(int slot) {
        for(int equipmentSlot : EQUIPMENT_SLOTS) {
            if(equipmentSlot == slot) return true;
        }
        return false;
    }
}
