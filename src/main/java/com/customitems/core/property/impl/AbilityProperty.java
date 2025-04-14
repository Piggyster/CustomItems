package com.customitems.core.property.impl;

import com.customitems.core.item.CustomItem;
import com.customitems.core.property.AbstractProperty;
import com.customitems.core.property.EventListener;
import com.customitems.core.property.LoreContributor;
import org.bukkit.ChatColor;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class AbilityProperty extends AbstractProperty implements EventListener<PlayerInteractEvent>, LoreContributor {

    private AbilityType type;
    private long lastUse;

    public AbilityProperty(AbilityType type) {
        this.type = type;
        lastUse = 0;
    }


    @Override
    public Class<PlayerInteractEvent> getEventClass() {
        return PlayerInteractEvent.class;
    }

    @Override
    public void handleEvent(PlayerInteractEvent event) {
        CustomItem item = getItem();
        if(item == null) return;

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getAction() != Action.RIGHT_CLICK_AIR) return;

        if(event.getItem() == null || !event.getItem().isSimilar(item.getItemStack())) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long cooldown = type.getCooldownSeconds() * 1000L;
        if(currentTime - lastUse < cooldown) {
            long remainingTime = (cooldown - (currentTime - lastUse)) / 1000;
            event.getPlayer().sendMessage("Cooldown for " + remainingTime + "s");
            return;
        }

        type.getExecutor().accept(event.getPlayer());

        lastUse = System.currentTimeMillis();
    }

    @Override
    public List<String> contributeLore() {
        return List.of(ChatColor.GOLD + "" + ChatColor.BOLD + "Ability: " + ChatColor.GREEN + type.getDisplayName());
    }

    @Override
    public String getType() {
        return "ability." + type.getInternalName();
    }
}
