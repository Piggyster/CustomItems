package com.customitems.core.stat;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.Item;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatStorage {

    private final Map<UUID, Map<StatType, Float>> playerStats;

    public StatStorage() {
        playerStats = new ConcurrentHashMap<>();
        Bukkit.getOnlinePlayers().forEach(this::recalculate);

        Bukkit.getScheduler().runTaskTimer(ItemPlugin.get(), () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                StringBuilder sb = new StringBuilder();
                for(StatType stat : StatType.values()) {
                    if(!sb.isEmpty()) sb.append(" ");
                    sb.append(stat.getColor()).append(stat.getDisplayName()).append(": ").append(getStat(player.getUniqueId(), stat));
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(sb.toString()));
            }
        }, 0, 20L);
    }

    public Map<StatType, Float> getStats(UUID player) {
        return playerStats.computeIfAbsent(player, id -> new ConcurrentHashMap<>());
    }

    public float getStat(UUID player, StatType statType) {
        return getStats(player).getOrDefault(statType, 0.0f);
    }

    public Map<StatType, Float> recalculate(Player player) {
        //Map<StatPhase, List<StatProvider>> providers = new EnumMap<>(StatPhase.class);
        Map<StatType, Float> stats = new ConcurrentHashMap<>();
        PlayerInventory inventory = player.getInventory();

        if(inventory.getItemInMainHand().getType() != Material.AIR) {
            mergeStatistics(stats, Item.of(inventory.getItemInMainHand()).getStatistics());
        }

        for(ItemStack armor : inventory.getArmorContents()) {
            if(armor == null || armor.getType().isAir()) continue;
            mergeStatistics(stats, Item.of(armor).getStatistics());
        }

        applyBaseStats(stats);

        return playerStats.put(player.getUniqueId(), stats);
    }

    private void applyBaseStats(Map<StatType, Float> stats) {
        for(StatType type : StatType.values()) {
            stats.merge(type, type.getBaseValue(), Float::sum);
        }
    }

    private void mergeStatistics(Map<StatType, Float> stats, ItemStatistics statistics) {
        for(StatType stat : statistics) {
            float value = statistics.getValue(stat);
            stats.put(stat, value + stats.getOrDefault(stat, 0.0f));
        }
    }
}
