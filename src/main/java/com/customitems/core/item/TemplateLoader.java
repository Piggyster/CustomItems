package com.customitems.core.item;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.property.Property;
import com.customitems.core.property.PropertyRegistry;
import com.google.gson.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateLoader {

    private final CustomItemsPlugin plugin;
    private final Gson gson;
    private final File templateDir;

    public TemplateLoader() {
        this.plugin = CustomItemsPlugin.getInstance();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.templateDir = new File(plugin.getDataFolder(), "templates");

        if(!templateDir.exists()) {
            templateDir.mkdirs();
            createExampleTemplate();
        }
    }

    public List<ItemTemplate> loadAllTemplates() {
        List<ItemTemplate> templates = new ArrayList<>();
        try {
            List<File> files = listJsonFiles(templateDir);
            for (File file : files) {
                try {
                    ItemTemplate template = loadTemplate(file);
                    if (template != null) {
                        templates.add(template);
                        plugin.getLogger().info("Loaded template: " + template.getId() + " from " + file.getName());
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to load template from " + file.getName(), e);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to list template files", e);
        }
        return templates;
    }

    public void loadTemplates() {
        List<ItemTemplate> templates = loadAllTemplates();
        for (ItemTemplate template : templates) {
            plugin.getItemManager().registerTemplate(template);
        }
        plugin.getLogger().info("Loaded " + templates.size() + " templates from JSON files");
    }

    private List<File> listJsonFiles(File dir) throws IOException {
        try (Stream<Path> stream = Files.walk(dir.toPath(), 1)) {
            return stream
                    .filter(path -> !path.equals(dir.toPath()))
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    private Supplier<Property> createPropertySupplier(String type, JsonObject json) {
        if (PropertyRegistry.hasJsonFactory(type)) {
            // Use the registered JSON factory for this property type
            return () -> PropertyRegistry.createPropertyFromJson(type, json);
        } else {
            // Unknown property type
            plugin.getLogger().warning("Unknown property type: " + type);
            return null;
        }
    }

    private ItemTemplate loadTemplate(File file) {
        try (FileReader reader = new FileReader(file)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            // Required fields
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
                    plugin.getLogger().warning("Invalid rarity in template " + id + ": " + rarityName);
                }
            }

            // Process properties
            if (json.has("properties")) {
                JsonArray propertiesArray = json.getAsJsonArray("properties");
                for (JsonElement element : propertiesArray) {
                    JsonObject propertyJson = element.getAsJsonObject();
                    String type = propertyJson.get("type").getAsString();

                    Supplier<Property> propertySupplier = createPropertySupplier(type, propertyJson);
                    if (propertySupplier != null) {
                        builder.addProperty(propertySupplier);
                    }
                }
            }

            return builder.build();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load template from file: " + file.getName(), e);
            return null;
        }
    }

    private void createExampleTemplate() {
        File exampleFile = new File(templateDir, "exotic_sword.json");

        JsonObject json = new JsonObject();
        json.addProperty("id", "exotic_sword");
        json.addProperty("material", "diamond_sword");
        json.addProperty("displayName", "Exotic Sword");
        json.addProperty("rarity", "epic");

        // Add properties
        JsonArray properties = new JsonArray();

        // Attribute
        JsonObject damageProperty = new JsonObject();
        damageProperty.addProperty("type", "attribute");
        damageProperty.addProperty("attributeType", "damage");
        damageProperty.addProperty("value", 15.0);
        properties.add(damageProperty);


        json.add("properties", properties);

        // Write to file
        try (FileWriter writer = new FileWriter(exampleFile)) {
            gson.toJson(json, writer);
            plugin.getLogger().info("Created example template: " + exampleFile.getName());
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to create example template", e);
        }
    }
}
