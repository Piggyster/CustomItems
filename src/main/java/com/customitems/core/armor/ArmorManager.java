package com.customitems.core.armor;

import com.customitems.core.ItemPlugin;
import com.customitems.core.component.impl.ArmorComponent;
import com.customitems.core.item.Item;
import com.customitems.core.item.template.Template;
import com.customitems.core.service.Service;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorManager implements Service {

    private Map<UUID, ArmorSet> activeSets;

    public ArmorManager() {
        activeSets = new ConcurrentHashMap<>();

        Bukkit.getScheduler().runTaskTimer(ItemPlugin.get(), () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                ArmorSet set = updateArmor(player);
                if(set != null) {
                    player.sendMessage("Current set: " + set.getName());
                }
            }
        }, 0, 20);
    }

    private ArmorSet updateArmor(Player player) {
        Template helmet = getTemplate(player, EquipmentSlot.HEAD);
        Template chestplate = getTemplate(player, EquipmentSlot.CHEST);
        Template leggings = getTemplate(player, EquipmentSlot.LEGS);
        Template boots = getTemplate(player, EquipmentSlot.FEET);

        ArmorSet set = ArmorSetRegistry.getSet(helmet, chestplate, leggings, boots);
        ArmorSet cachedSet = activeSets.get(player.getUniqueId());

        if(set == null && cachedSet != null) {
            //removing set
            cachedSet.onUnequip(player);
            activeSets.remove(player.getUniqueId());
        } else if(set != null && cachedSet == null) {
            //adding set
            set.onEquip(player);
            activeSets.put(player.getUniqueId(), set);
        } else if(set != null && !cachedSet.equals(set)) {
            //swapping set
            set.onEquip(player);
            cachedSet.onEquip(player);
            activeSets.put(player.getUniqueId(), set);
        }

        for(ItemStack stack : player.getInventory().getArmorContents()) {
            if(stack == null || stack.getType() == Material.AIR) continue;
            Item item = Item.of(stack);
            if(item == null) continue;
            item.update(player, stack);
        }

        return set;
    }

    private Template getTemplate(Player player, EquipmentSlot slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        if(stack == null || stack.getType() == Material.AIR) return null;

        Item item = Item.of(stack);
        if(item == null || !item.hasComponent(ArmorComponent.class)) return null;
        return item.getTemplate();
    }
}
