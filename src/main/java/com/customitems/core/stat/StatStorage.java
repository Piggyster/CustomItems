package com.customitems.core.stat;

import com.customitems.core.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatStorage {

    private final Map<UUID, Map<StatType, Double>> playerStats;

    public StatStorage() {
        playerStats = new ConcurrentHashMap<>();
        Bukkit.getOnlinePlayers().forEach(this::recalculate);
    }

    public Map<StatType, Double> getStats(UUID player) {
        return playerStats.computeIfAbsent(player, id -> new ConcurrentHashMap<>());
    }

    public double getStat(UUID player, StatType statType) {
        return getStats(player).getOrDefault(statType, 0.0);
    }

    public Map<StatType, Double> recalculate(Player player) {
        Map<StatPhase, List<StatProvider>> providers = new EnumMap<>(StatPhase.class);
        Map<StatType, Double> stats = new ConcurrentHashMap<>();
        PlayerInventory inventory = player.getInventory();

        if(inventory.getItemInMainHand().getType() != Material.AIR) {
            fetchProviders(Item.of(inventory.getItemInMainHand()), providers);
        }

        for(ItemStack armor : inventory.getArmorContents()) {
            if(armor == null || armor.getType().isAir()) continue;
            fetchProviders(Item.of(armor), providers);
        }

        applyBaseStats(stats);

        calculateStats(providers, stats);

        return playerStats.put(player.getUniqueId(), stats);
    }

    private void applyBaseStats(Map<StatType, Double> stats) {
        for(StatType type : StatType.values()) {
            stats.merge(type, type.getBaseValue(), Double::sum);
        }
    }

    private void fetchProviders(Item item, Map<StatPhase, List<StatProvider>> providers) {
        if(item == null) return;

        //for(Property property : item.getProperties()) {
        //    if(property instanceof StatProvider provider) {
        //        providers.computeIfAbsent(provider.getPhase(),
        //                phase -> new ArrayList<>()).add(provider);
        //    }
        //}
    }

    private void calculateStats(Map<StatPhase, List<StatProvider>> providers, Map<StatType, Double> stats) {
        for(StatPhase phase : StatPhase.values()) {
            List<StatProvider> phaseProviders = providers.getOrDefault(phase, Collections.emptyList());
            for(StatProvider provider : phaseProviders) {
                provider.applyStats(stats);
            }
        }
    }
}
