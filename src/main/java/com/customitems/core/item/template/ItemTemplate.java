package com.customitems.core.item.template;

import com.customitems.core.component.Component;
import com.customitems.core.item.ItemRarity;
import com.customitems.core.stat.ItemStatistics;
import com.customitems.core.stat.StatType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Implementation of the Template interface.
 * This class represents a template for creating items with specific properties.
 * It includes methods to get the item's ID, material, display name, rarity, and default properties.
 */

public class ItemTemplate implements Template {

    private final String id;
    private final Material material;
    private final String displayName;
    private final ItemRarity rarity;
    private final ItemStatistics statistics;
    private final Map<Class<? extends Component>, Component> components;

    public ItemTemplate(@NotNull String id, @NotNull Material material, @NotNull String displayName,
                        @NotNull ItemRarity rarity, @NotNull ItemStatistics statistics,
                        @NotNull  Map<Class<? extends Component>, Component> components) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.rarity = rarity;
        this.statistics = statistics;
        this.components = components;
    }

    public static class Builder {
        private final String id;
        private String displayName;
        private Material material = Material.STONE;
        private ItemRarity rarity = ItemRarity.COMMON;
        private final Map<StatType, Float> statistics = new HashMap<>();
        private final Map<Class<? extends Component>, Component> components = new HashMap<>();
        private String texture;

        public Builder(String id) {
            this.id = id;
            this.displayName = id;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public Builder rarity(ItemRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder addStatistic(StatType stat, float value) {
            statistics.put(stat, value);
            return this;
        }

        public Builder addComponent(Component component) {
            components.put(component.getClass(), component);

            component.getInherited().forEach(inherited -> {
                components.putIfAbsent(inherited.getClass(), inherited); //TODO add check to not override none explicit values
            });
            return this;
        }

        public Builder texture(String texture) {
            this.texture = texture;
            return this;
        }

        public ItemTemplate build() {
            ItemStatistics statistics = new ItemStatistics(this.statistics);
            if(material.equals(Material.PLAYER_HEAD) && texture != null && !texture.isEmpty()) {
                return new SkullTemplate(id, texture, displayName, rarity, statistics, components);
            }
            return new ItemTemplate(id, material, displayName, rarity, statistics, components);
        }
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public ItemRarity getRarity() {
        return rarity;
    }

    @Override
    public ItemStatistics getStatistics() {
        return statistics;
    }

    @Override
    public Map<Class<? extends Component>, Component> getComponents() {
        return components;
    }

    @Override
    public ItemStack createItemStack() { //TODO
        ItemStack stack = new ItemStack(material);
        return stack;
    }

    @Override
    public boolean isVanilla() {
        return false;
    }

}
