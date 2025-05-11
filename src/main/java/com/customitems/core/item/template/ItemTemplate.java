package com.customitems.core.item.template;

import com.customitems.core.item.ItemRarity;
import com.customitems.core.property.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
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
    private final Set<Supplier<Property>> defaultPropertySuppliers;

    public ItemTemplate(@NotNull String id, @NotNull Material material, @NotNull String displayName,
                        @NotNull ItemRarity rarity, @NotNull Set<Supplier<Property>> defaultPropertySuppliers) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.rarity = rarity;
        this.defaultPropertySuppliers = defaultPropertySuppliers;
    }

    public static class Builder {
        private final String id;
        private String displayName;
        private Material material = Material.STONE;
        private ItemRarity rarity = ItemRarity.COMMON;
        private Set<Supplier<Property>> defaultPropertySuppliers = new HashSet<>();

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

        public Builder addProperty(Supplier<Property> propertySupplier) {
            if(propertySupplier == null) return this;
            this.defaultPropertySuppliers.add(propertySupplier);
            return this;
        }

        public ItemTemplate build() {
            return new ItemTemplate(id, material, displayName, rarity, defaultPropertySuppliers);
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

    /**
     * Get the default properties of this item template.
     * @return New instances of default properties from suppliers
     */
    @Override
    public Set<Property> getDefaultProperties() { //TODO
        Set<Property> properties = new HashSet<>();
        for(Supplier<Property> supplier : defaultPropertySuppliers) {
            properties.add(supplier.get());
        }
        return properties;
    }

    @Override
    public ItemStack createItemStack() { //TODO
        return new ItemStack(material);
    }

    @Override
    public boolean isVanilla() {
        return false;
    }

}
