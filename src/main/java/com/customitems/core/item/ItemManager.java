package com.customitems.core.item;

import com.customitems.core.CustomItemsPlugin;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ItemManager {

    private final CustomItemsPlugin plugin;
    private final Map<String, ItemTemplate> templates;
    private final Map<Material, VanillaItemTemplate> vanillaTemplates;
    private final TemplateLoader templateLoader;
    private final VanillaTemplateLoader vanillaTemplateLoader;

    private final Map<ItemStack, CustomItem> cache;

    public ItemManager() {
        plugin = CustomItemsPlugin.getInstance();
        templates = new ConcurrentHashMap<>();
        vanillaTemplates = new ConcurrentHashMap<>();
        templateLoader = new TemplateLoader();
        vanillaTemplateLoader = new VanillaTemplateLoader();
        cache = new WeakHashMap<>();
    }


    public void loadTemplatesFromJson() {
        List<ItemTemplate> loadedTemplates = templateLoader.loadAllTemplates();
        for (ItemTemplate template : loadedTemplates) {
            registerTemplate(template);
        }
        plugin.getLogger().info("Loaded " + loadedTemplates.size() + " templates from JSON files");

        List<VanillaItemTemplate> loadedVanillaTemplates = vanillaTemplateLoader.loadAllTemplates();
        for (VanillaItemTemplate template : loadedVanillaTemplates) {
            vanillaTemplates.put(template.getMaterial(), template);
        }
        plugin.getLogger().info("Loaded " + loadedVanillaTemplates.size() + " vanilla templates from JSON files");
    }

    public boolean registerTemplate(ItemTemplate template) {
        if (templates.containsKey(template.getId())) {
            return false;
        }
        templates.put(template.getId(), template);
        return true;
    }

    public ItemTemplate getTemplate(String id) {
        return templates.get(id);
    }

    protected CustomItem createItem(String templateId) {
        ItemTemplate template = templates.get(templateId);
        if (template == null) {
            return null;
        }
        return new CustomItem(template);
    }

    public CustomItem getCustomItem(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        CustomItem item = cache.get(itemStack);
        if(item != null) return item;

        // Check if this is a custom item
        if (isCustomItem(itemStack)) {

            String templateId = NBT.get(itemStack, nbt -> {
                return nbt.getString("item_type");
            });

            if (templateId != null) {
                ItemTemplate template = templates.get(templateId);
                if (template != null) {
                    item = new CustomItem(itemStack, template);
                    cache.put(itemStack, item);
                    return item;
                }
            }
        }

        // If not a custom item or template not found, use the vanilla template
        VanillaItemTemplate vanillaTemplate = getVanillaTemplate(itemStack.getType());
        return new CustomItem(itemStack, vanillaTemplate);
    }

    public boolean isCustomItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }

        return NBT.get(itemStack, nbt -> {
            return nbt.hasTag("item_type");
        });
    }



    public VanillaItemTemplate getVanillaTemplate(Material material) {
        VanillaItemTemplate template = vanillaTemplates.get(material);
        if(template != null) {
            return template;
        }
        template = VanillaItemTemplate.fromMaterial(material);
        return template;
    }

}
