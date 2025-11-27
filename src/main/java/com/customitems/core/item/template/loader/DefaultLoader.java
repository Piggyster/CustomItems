package com.customitems.core.item.template.loader;

import com.customitems.core.ItemPlugin;
import com.customitems.core.component.Component;
import com.customitems.core.component.ComponentRegistry;
import com.customitems.core.item.ItemRarity;
import com.customitems.core.item.template.ItemTemplate;
import com.customitems.core.item.template.Template;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * TemplateLoader is responsible for loading item templates from a specified directory.
 * It creates the directory if it does not exist.
 */

public class DefaultLoader extends AbstractLoader {

    public DefaultLoader(@NotNull File directory) {
        super(directory);
    }

    public List<Template> loadAllTemplates() {
        List<Template> templates = new ArrayList<>();
        try {
            List<File> files = listJsonFiles(directory);
            for (File file : files) {
                try {
                    Template template = loadTemplate(file);
                    if (template != null) {
                        templates.add(template);
                        Bukkit.getLogger().info("Loaded template: " + template.getId() + " from " + file.getName());
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Failed to load template from " + file.getName() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to list template files: " + e.getMessage());
        }
        return templates;
    }


    @Override
    public Template loadTemplate(File file) {
        try (FileReader reader = new FileReader(file)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            String id = json.get("id").getAsString();
            String materialName = json.get("material").getAsString();
            Material material = Material.valueOf(materialName.toUpperCase());

            // Create template builder
            ItemTemplate.Builder builder = new ItemTemplate.Builder(id)
                    .material(material);

            // Optional fields
            if (json.has("displayName")) {
                String displayName = json.get("displayName").getAsString();
                // Process color codes if the display name uses & for formatting
                displayName = ChatColor.translateAlternateColorCodes('&', displayName);
                builder.displayName(displayName);
            }

            if (json.has("rarity")) {
                String rarityName = json.get("rarity").getAsString();
                try {
                    ItemRarity rarity = ItemRarity.valueOf(rarityName.toUpperCase());
                    builder.rarity(rarity);
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid rarity in template " + id + ": " + rarityName);
                }
            }

            // Process components
            if (json.has("components")) {
                JsonObject componentsJson = json.getAsJsonObject("components");
                for(String key : componentsJson.keySet()) {
                    ItemPlugin.get().getLogger().warning("Attempting to load " + key + " component");
                    JsonElement componentJson = componentsJson.get(key);
                    Component component = ComponentRegistry.deserialize(key, componentJson);

                    if(component == null) continue;
                    ItemPlugin.get().getLogger().warning("Found component deserializer");

                    builder.addComponent(component);
                }
            }

            if(json.has("texture")) {
                String texture = json.get("texture").getAsString();
                builder.texture(texture);
            }

            return builder.build();
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to load template from file: " + file.getName() + " - " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void createExampleTemplate() {
        File exampleFile = new File(directory, "exotic_sword.json");

        JsonObject json = new JsonObject();
        json.addProperty("id", "exotic_sword");
        json.addProperty("material", "diamond_sword");
        json.addProperty("displayName", "Exotic Sword");
        json.addProperty("rarity", "rare");


        // Add properties
        JsonObject components = new JsonObject();

        // Attribute
        JsonObject statProperty = new JsonObject();

        statProperty.addProperty("damage", 15);
        statProperty.addProperty("mana", 100);

        components.add("unique", new JsonObject());

        json.add("components", components);

        // Write to file
        try (FileWriter writer = new FileWriter(exampleFile)) {
            gson.toJson(json, writer);
            Bukkit.getLogger().info("Created example template: " + exampleFile.getName());
        } catch (IOException ex) {
            Bukkit.getLogger().warning("Failed to create example template: " + exampleFile.getName() + " - " + ex.getMessage());
        }
    }
}
