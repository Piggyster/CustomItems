package com.customitems.core.item;

import com.customitems.core.property.Property;
import org.bukkit.Material;

import java.util.List;
import java.util.function.Supplier;

public class VanillaItemTemplate extends ItemTemplate {


    public static VanillaItemTemplate fromMaterial(Material material) {
        return new VanillaItemTemplate(material);
    }

    public static String getNameFromMaterial(Material material) {
        if (material == null) {
            return "Unknown Item";
        }
        String input = material.toString();

        // Replace underscores with spaces
        String withSpaces = input.replace("_", " ");

        // Split into words
        String[] words = withSpaces.split("\\s+");
        StringBuilder result = new StringBuilder();

        // Process each word
        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize first letter, lowercase the rest
                String capitalizedWord = word.substring(0, 1).toUpperCase() +
                        word.substring(1).toLowerCase();
                result.append(capitalizedWord).append(" ");
            }
        }

        // Remove trailing space if present
        if (!result.isEmpty()) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }




    public VanillaItemTemplate(Material material) {
        super("vanilla:" + material.toString().toLowerCase(),
                getNameFromMaterial(material),
                material,
                ItemRarity.COMMON,
                List.of());
    }
}
