package com.customitems.core.item;

import com.customitems.core.ItemPlugin;
import com.customitems.core.component.impl.BackpackComponent;
import com.customitems.core.component.impl.RecipeComponent;
import com.customitems.core.item.template.Template;
import com.customitems.core.item.template.loader.DefaultLoader;
import com.customitems.core.item.template.VanillaTemplate;
import com.customitems.core.item.template.loader.TemplateLoader;
import com.customitems.core.service.Services;
import com.google.common.collect.ImmutableList;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemManager {

    private final Map<String, Template> templates;

    private final Map<UUID, Item> strongCache;
    private final Map<ItemStack, Item> weakCache;

    public ItemManager() {
        templates = new ConcurrentHashMap<>();
        strongCache = new ConcurrentHashMap<>();
        weakCache = new WeakHashMap<>();

        ItemPlugin plugin = ItemPlugin.get();


        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if(itemStack == null || itemStack.getType() == Material.AIR) continue;
                Item item = Item.of(itemStack);

                if(item.hasComponent(BackpackComponent.class)) continue;
                item.update(player, itemStack);
            }
        }, 0, 20L);
    }

    public void loadTemplates() {
        DefaultLoader loader = new DefaultLoader(new File(ItemPlugin.get().getDataFolder(), "templates"));
        List<Template> templates = loader.loadAllTemplates();

        templates.forEach(this::registerTemplate);

        templates.forEach(template -> {
            if(template.getComponents().containsKey(RecipeComponent.class)) {
               RecipeComponent component = (RecipeComponent) template.getComponents().get(RecipeComponent.class);
               component.postInit();
            }
        });
    }

    public void registerTemplate(Template template) {
        templates.put(template.getId(), template);
    }

    public Template getTemplate(String id) {
        Template template = templates.get(id);
        if(template == null) {
            Material material = Material.getMaterial(id.toUpperCase());
            if(material == null) return null;
            return new VanillaTemplate(material);
        }
        return template;
    }

    public List<Template> getTemplates() {
        return ImmutableList.copyOf(templates.values());
    }

    //TODO more improvements
    public Item getItem(ItemStack stack) {
        Template template = extractTemplate(stack);
        if(template.isVanilla()) {
            return new Item(stack, template);
        }
        return new Item(stack, template);
        /*UUID uuid = extractUUID(stack);
        if(uuid != null) {
            Item item = strongCache.computeIfAbsent(uuid, id -> new Item(stack, template));
            item.bind(stack);
            return item;
        } else {
            return weakCache.computeIfAbsent(stack, s -> new Item(stack, template));
        }*/
    }

    public UUID extractUUID(ItemStack itemStack) {
        return NBT.get(itemStack, nbt -> {
            if(!nbt.hasTag("properties")) return null;
            ReadableNBT propertyNbt = nbt.getCompound("properties");
            if(!propertyNbt.hasTag("uuid")) return null;
            return propertyNbt.getUUID("uuid");
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

    public Item deserialize(byte[] bytes) {
        ItemManager itemManager = Services.get(ItemManager.class);
        try(DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            while(in.available() > 0) {
                String templateId = in.readUTF();
                int persistentDataLength = in.readInt();
                byte[] persistentData = in.readNBytes(persistentDataLength);

                Template template = itemManager.getTemplate(templateId);
                if(template == null) return null;

                ItemStack stack = template.createItemStack();
                if(persistentDataLength > 0) {
                    NBT.modify(stack, nbt -> {
                        ReadWriteNBT attributeNbt = nbt.getOrCreateCompound("attributes");
                        attributeNbt.mergeCompound(NBT.parseNBT(new String(persistentData, StandardCharsets.UTF_8)));
                    });
                }
                return new Item(stack, template);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
