package com.customitems.core.item;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.template.Template;
import com.customitems.core.item.template.TemplateLoader;
import com.customitems.core.item.template.VanillaTemplate;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ItemManager {

    private final Map<String, Template> templates;

    private final Map<ItemStack, Item> cache;

    public ItemManager() {
        templates = new ConcurrentHashMap<>();
        cache = new WeakHashMap<>();

        ItemPlugin plugin = ItemPlugin.get();

        TemplateLoader loader = new TemplateLoader(new File(plugin.getDataFolder(), "templates"));
        loader.loadAllTemplates().forEach(this::registerTemplate);
    }

    public void registerTemplate(Template template) {
        templates.put(template.getId(), template);
    }

    public Template getTemplate(String id) {
        return templates.get(id);
    }

    public Item getItem(ItemStack itemStack) {
        return cache.computeIfAbsent(itemStack, stack -> {

            Template template = extractTemplate(stack);

            if (template != null) {
                return new Item(stack, template);
            }
            return null;
        });
    }

    public Template extractTemplate(ItemStack itemStack) {
        String id = NBT.get(itemStack, nbt -> {
            if(nbt.hasTag("item_id")) {
                return nbt.getString("item_id");
            }
            return null;
        });

        if(id == null || !templates.containsKey(id)) {
            return new VanillaTemplate(itemStack.getType());
        } else {
            return getTemplate(id);
        }
    }
}
