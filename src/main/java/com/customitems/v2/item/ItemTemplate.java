package com.customitems.v2.item;

import com.customitems.v2.property.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

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
