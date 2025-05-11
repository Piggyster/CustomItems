package com.customitems.core.item.template;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.ItemRarity;
import com.customitems.core.property.Property;
import com.customitems.core.property.PropertyRegistry;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TemplateLoader is responsible for loading item templates from a specified directory.
 * It creates the directory if it does not exist.
 */

public class TemplateLoader {

    protected final File directory;
    protected final PropertyRegistry registry;
    protected final Gson gson;


    public TemplateLoader(@NotNull File directory) {
        this.directory = directory;
        registry = ItemPlugin.get().getRegistry();
        gson = new GsonBuilder().setPrettyPrinting().create();

        if(!directory.exists()) {
            directory.mkdirs();
            createExampleTemplate();
        }
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

    protected List<File> listJsonFiles(File dir) throws IOException {
        try (Stream<Path> stream = Files.walk(dir.toPath(), 1)) {
            return stream
                    .filter(path -> !path.equals(dir.toPath()))
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    private Supplier<Property> createPropertySupplier(String type, JsonObject json) {
        Class<? extends Property> clazz = registry.getClass(type);

        if(clazz == null || !registry.hasJsonFactory(clazz)) {
            Bukkit.getLogger().warning("Unknown property type: " + type);
            return null;
        }

        return () -> registry.fromJson(clazz, json);
    }

    protected Template loadTemplate(File file) {
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

            // Process properties
            if (json.has("properties")) {
                JsonObject propertiesJson = json.getAsJsonObject("properties");
                for(String key : propertiesJson.keySet()) {
                    JsonObject propertyJson = propertiesJson.getAsJsonObject(key);

                    Supplier<Property> propertySupplier = createPropertySupplier(key, propertyJson);
                    if(propertySupplier != null) {
                        builder.addProperty(propertySupplier);
                    }
                }
            }

            return builder.build();
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to load template from file: " + file.getName() + " - " + ex.getMessage());
            return null;
        }
    }

    protected void createExampleTemplate() {
        File exampleFile = new File(directory, "exotic_sword.json");

        JsonObject json = new JsonObject();
        json.addProperty("id", "exotic_sword");
        json.addProperty("material", "diamond_sword");
        json.addProperty("displayName", "Exotic Sword");
        json.addProperty("rarity", "epic");



        // Add properties
        JsonObject properties = new JsonObject();

        // Attribute
        JsonObject statProperty = new JsonObject();

        statProperty.addProperty("damage", 15);
        statProperty.addProperty("mana", 100);

        properties.add("stats", statProperty);

        json.add("properties", properties);

        // Write to file
        try (FileWriter writer = new FileWriter(exampleFile)) {
            gson.toJson(json, writer);
            Bukkit.getLogger().info("Created example template: " + exampleFile.getName());
        } catch (IOException ex) {
            Bukkit.getLogger().warning("Failed to create example template: " + exampleFile.getName() + " - " + ex.getMessage());
        }
    }
}
