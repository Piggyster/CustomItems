package com.customitems.core.item.template;

import com.customitems.core.item.ItemRarity;
import com.customitems.core.property.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Represents a vanilla item template.
 * This class is used to create item stacks of vanilla items.
 * It's lightweight and does not require any additional data.
 */

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
