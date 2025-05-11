package com.customitems.v2.item;

import com.customitems.v2.property.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class VanillaTemplate implements Template {

    private final Material material;

    public VanillaTemplate(@NotNull Material material) {
        this.material = material;
    }

    @Override
    public String getId() {
        return "vanilla:" + material.toString().toLowerCase();
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public String getDisplayName() {
        return material.toString();
    }

    @Override
    public ItemRarity getRarity() {
        return ItemRarity.COMMON;
    }

    @Override
    public Set<Property> getDefaultProperties() {
        return Set.of();
    }

    @Override
    public ItemStack createItemStack() {
        return new ItemStack(material);
    }

    @Override
    public boolean isVanilla() {
        return true;
    }
}
