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
    private final String displayName;

    public VanillaTemplate(@NotNull Material material) {
        this.material = material;
        displayName = capitalizeWords(material.toString());
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
        return displayName;
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

    private static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) return input;

        input = input.replace("_", " ");

        StringBuilder out = new StringBuilder(input.length());
        boolean capNext = true;

        for (char ch : input.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                capNext = true;
                out.append(ch);
            } else {
                out.append(capNext ? Character.toUpperCase(ch)
                        : Character.toLowerCase(ch));
                capNext = false;
            }
        }
        return out.toString();
    }
}
