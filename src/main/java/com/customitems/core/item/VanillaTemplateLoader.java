package com.customitems.core.item;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.property.Property;
import com.customitems.core.property.PropertyRegistry;
import com.google.gson.*;
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

public class VanillaTemplateLoader {

    private final CustomItemsPlugin plugin;
    private final Gson gson;
    private final File templateDir;


    public VanillaTemplateLoader() {
        this.plugin = CustomItemsPlugin.getInstance();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.templateDir = new File(plugin.getDataFolder(), "vanilla_templates");

        if(!templateDir.exists()) {
            templateDir.mkdirs();
            createExampleTemplate();
        }
    }

    public List<VanillaItemTemplate> loadAllTemplates() {
        List<VanillaItemTemplate> templates = new ArrayList<>();
        try {
            List<File> files = listJsonFiles(templateDir);
            for (File file : files) {
                try {
                    VanillaItemTemplate template = loadTemplate(file);
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
            return () -> PropertyRegistry.fromJson(type, json);
        } else {
            // Unknown property type
            plugin.getLogger().warning("Unknown property type: " + type);
            return null;
        }
    }

    private VanillaItemTemplate loadTemplate(File file) {
        try(FileReader reader = new FileReader(file)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            String materialName = json.get("material").getAsString();
            Material material = Material.valueOf(materialName.toUpperCase());


            ItemRarity rarity = ItemRarity.COMMON;
            if (json.has("rarity")) {
                String rarityName = json.get("rarity").getAsString();
                try {
                    rarity = ItemRarity.valueOf(rarityName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid rarity in template " + materialName + ": " + rarityName);
                }
            }

            List<Supplier<Property>> suppliers = new ArrayList<>();

            if (json.has("properties")) {
                JsonArray propertiesArray = json.getAsJsonArray("properties");
                for (JsonElement element : propertiesArray) {
                    JsonObject propertyJson = element.getAsJsonObject();
                    String type = propertyJson.get("type").getAsString();

                    Supplier<Property> propertySupplier = createPropertySupplier(type, propertyJson);
                    if (propertySupplier != null) {
                        suppliers.add(propertySupplier);
                    }
                }
            }

            return new VanillaItemTemplate(material, rarity, suppliers);
        } catch(IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to load template from file: " + file.getName(), ex);
            return null;
        }
    }

    private void createExampleTemplate() {
        File exampleFile = new File(templateDir, "iron_sword.json");

        JsonObject json = new JsonObject();
        json.addProperty("material", "iron_sword");
        json.addProperty("rarity", "uncommon");

        // Add properties
        JsonArray properties = new JsonArray();

        // Attribute
        JsonObject damageProperty = new JsonObject();
        damageProperty.addProperty("type", "attribute");
        damageProperty.addProperty("attributeType", "damage");
        damageProperty.addProperty("value", 5.0);
        properties.add(damageProperty);


        json.add("properties", properties);

        // Write to file
        try (FileWriter writer = new FileWriter(exampleFile)) {
            gson.toJson(json, writer);
            plugin.getLogger().info("Created example template: " + exampleFile.getName());
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to create example template", ex);
        }
    }
}
