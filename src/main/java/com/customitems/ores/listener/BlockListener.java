package com.customitems.ores.listener;

import com.customitems.core.ItemPlugin;
import com.customitems.core.component.impl.PickaxeComponent;
import com.customitems.core.item.Item;
import com.customitems.core.service.Services;
import com.customitems.ores.MiningSession;
import com.customitems.ores.OreManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockListener implements Listener {

    @EventHandler
    public void onEvent(BlockDamageEvent event) {
        event.getPlayer().sendMessage("Damaging block");
        ItemStack stack = event.getItemInHand();
        if(stack.getType() == Material.AIR) return;


        Item item = Item.of(stack);
        if(!item.hasComponent(PickaxeComponent.class)) return;
        PickaxeComponent component = item.getComponent(PickaxeComponent.class);

        event.setCancelled(true);
        if(event.getBlock().getType() == Material.BEDROCK) return;

        OreManager oreManager = Services.get(OreManager.class);
        oreManager.createSession(event.getPlayer(), event.getBlock(), item);
        //event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100000, 3));
    }

    @EventHandler
    public void onEvent(BlockDamageAbortEvent event) {
        event.getPlayer().sendMessage("Not Damaging block");
        ItemStack stack = event.getItemInHand();
        if(stack.getType() == Material.AIR) return;

        Item item = Item.of(stack);
        if(!item.hasComponent(PickaxeComponent.class)) return;
        //event.getPlayer().removePotionEffect(PotionEffectType.MINING_FATIGUE);
        PickaxeComponent component = item.getComponent(PickaxeComponent.class);

        OreManager oreManager = Services.get(OreManager.class);
        MiningSession session = oreManager.removeSession(event.getPlayer().getUniqueId());
        if(session != null) {
            session.end();
        }
    }

    @EventHandler
    public void onEvent(PlayerItemHeldEvent event) {
        ItemStack newStack = event.getPlayer().getInventory().getItem(event.getNewSlot());
        ItemStack oldStack = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        if((newStack == null || newStack.getType() == Material.AIR) && (oldStack == null || oldStack.getType() == Material.AIR)) return;

        if(oldStack != null) {
            Item item = Item.of(oldStack);
            if(item.hasComponent(PickaxeComponent.class)) {
                event.getPlayer().removePotionEffect(PotionEffectType.MINING_FATIGUE);
            }
        }

        if(newStack != null) {
            Item item = Item.of(newStack);
            if(item.hasComponent(PickaxeComponent.class)) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, -1, 3));
            }
        }
    }
}
