package com.customitems.core.item;

import com.customitems.core.property.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ItemTemplate {

    private final String id;
    private final String displayName;
    private final Material material;
    private final ItemRarity rarity;
    private final List<Supplier<Property>> defaultPropertySuppliers;

    public static class Builder {
        private final String id;
        private String displayName;
        private Material material = Material.STONE;
        private ItemRarity rarity = ItemRarity.COMMON;
        private List<Supplier<Property>> defaultPropertySuppliers = new ArrayList<>();

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
            this.defaultPropertySuppliers.add(propertySupplier);
            return this;
        }

        public ItemTemplate build() {
            return new ItemTemplate(id, displayName, material, rarity, defaultPropertySuppliers);
        }
    }

    protected ItemTemplate(String id, String displayName, Material material, ItemRarity rarity,
                        List<Supplier<Property>> defaultPropertySuppliers) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.rarity = rarity;
        this.defaultPropertySuppliers = defaultPropertySuppliers;
    }

    public ItemStack createItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    public Collection<Property> getDefaultProperties() {
        List<Property> properties = new ArrayList<>();
        for (Supplier<Property> supplier : defaultPropertySuppliers) {
            properties.add(supplier.get());
        }
        return properties;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemRarity getRarity() {
        return rarity;
    }


}
