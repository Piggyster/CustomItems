package com.customitems.core.item;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.template.Template;
import com.customitems.core.property.*;
import com.customitems.core.property.EventListener;
import com.customitems.core.service.Services;
import com.google.common.collect.ImmutableList;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a customitem in the game.
 * This class is responsible for managing the properties of the item and its associated template.
 * It also provides methods to load, save, and update the item's display.
 *
 * @see Template
 * @see Property
 */

public class Item {

    private Template template;
    private ItemStack owner;
    private Map<PropertyType<?>, Property> properties;
    private List<EventListener> listeners;

    public static Item of(ItemStack itemStack) {
        ItemManager itemManager = Services.get(ItemManager.class);
        return itemManager.getItem(itemStack);
    }

    public static Item of(byte[] bytes) {
        ItemManager itemManager = Services.get(ItemManager.class);
        return itemManager.deserialize(bytes);
    }

    public Item(@NotNull Template template) {
        this(template.createItemStack(), template);
    }

    public Item(@NotNull ItemStack owner, @NotNull Template template) {
        Objects.requireNonNull(owner);
        Objects.requireNonNull(template);
        this.owner = owner;
        this.template = template;
        properties = new ConcurrentHashMap<>();
        listeners = new ArrayList<>();

        load();

        if(mark() || !owner.hasItemMeta()) {
            updateDisplay();
        }
    }


    /**
     * Marks the item with the template identifier.
     *
     * @return true if the item was marked, false if it was already marked
     */
    private boolean mark() {
        if(template.isVanilla()) return false;
        return NBT.modify(owner, nbt -> {
            if(!nbt.hasTag("item_id")) {
                nbt.setString("item_id", template.getId());
                return true;
            } else {
                return false;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void handleEvent(T event) {
        for(EventListener listener : listeners) {
            if(listener.getEvents().contains(event.getClass())) {
                listener.handle(event);
            }
        }
    }

    /**
     *
     */
    public void bind(@NotNull ItemStack stack) {
        owner = stack;
    }


    /**
     * Loads an item's properties from NBT data as well as
     * the default properties from the template.
     */
    public void load() {
        properties.clear();

        Map<Class<? extends Property>, Property> rawProperties = new HashMap<>();

        //load properties from nbt
        NBT.modify(owner, nbt -> {

            ReadWriteNBT propertyNbt = nbt.hasTag("properties") ? nbt.getCompound("properties") : null;
            PropertyRegistry registry = Services.get(PropertyRegistry.class);

            if(propertyNbt != null) {
                for(String id : propertyNbt.getKeys()) {
                    Property property = registry.fromNbt(id, propertyNbt);
                    if(property == null) continue;
                    rawProperties.put(property.getClass(), property);
                }
            }

            for(Property property : template.getDefaultProperties()) {
                Property existing = rawProperties.putIfAbsent(property.getClass(), property);
                if(existing instanceof MergeableProperty mergeableProperty) {
                    mergeableProperty.merge(property);
                }
            }


            if(rawProperties.isEmpty()) {
                nbt.removeKey("properties");
            } else if(!nbt.hasTag("properties")) {
                propertyNbt = nbt.getOrCreateCompound("properties");
            }


            //rawProperties.sort(Comparator.comparingInt(p -> p.getPriority().ordinal())); //TODO
            boolean dirty = false; //might need to be outside lambda and be atomic

            for(Map.Entry<Class<? extends Property>, Property> entry : rawProperties.entrySet()) {
                Property property = entry.getValue();
                addProperty(property);
                if(property instanceof PersistentProperty persistentProperty) {
                    dirty = persistentProperty.load(propertyNbt);
                }
            }

            if(dirty) save();


        });
        //rawProperties.forEach(this::addProperty);
    }


    /**
     * Saves the item's properties to NBT data.
     * Only applies to persistent properties.
     */
    public void save() {
        NBT.modify(owner, nbt -> {
            if(properties.isEmpty() && nbt.hasTag("properties")) {
                nbt.removeKey("proerties");
                return;
            }
            ReadWriteNBT propertyNbt = nbt.getOrCreateCompound("properties");
            boolean executed = false;
            for(Property property : properties.values()) {
                if(property instanceof PersistentProperty persistentProperty) {
                    persistentProperty.save(propertyNbt);
                    executed = true;
                }
            }

            if(!executed) {
                nbt.removeKey("properties");
            }
        });
    }


    /**
     * Updates the item's display name and lore based on its properties.
     * This method should be called when changes are made to the item's properties
     */
    public void updateDisplay() {
        ItemMeta meta = owner.getItemMeta();
        if(meta == null) return;
        //do meta operations
        ItemRarity rarity = template.getRarity();

        meta.setDisplayName(rarity.getColor() + template.getDisplayName());

        List<LoreContributor> loreContributors = properties.values().stream()
                .filter(p -> p instanceof LoreContributor)
                .map(p -> (LoreContributor) p)
                .sorted(Comparator.comparingInt(LoreContributor::getLorePriority).reversed())
                .toList();

        LoreVisitor visitor = new LoreVisitor();

        loreContributors.forEach(contributor -> contributor.contributeLore(visitor));

        visitor.visit("");
        visitor.visit(rarity.getColor().toString() + ChatColor.BOLD + rarity.getDisplayName().toUpperCase());

        meta.setLore(visitor.getLore());

        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        owner.setItemMeta(meta);
        owner.setType(template.getMaterial());
    }

    public boolean addProperty(@NotNull Property property) {
        PropertyType<? extends Property> type = property.getType();
        if(hasProperty(type)) {
            return false;
        }
        properties.put(type, property);
        property.init(this);

        if(property instanceof EventListener listener) {
            listeners.add(listener);
        }
        return true;
    }

    public boolean hasProperty(PropertyType<? extends Property> type) {
        return properties.containsKey(type);
    }

    public boolean hasProperty(Property property) {
        return properties.containsValue(property);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Property> T getProperty(@NotNull PropertyType<? extends T> type) {
        return (T) properties.get(type);
    }

    public List<Property> getProperties() {
        return ImmutableList.copyOf(properties.values());
    }

    public void removeProperty(String type) {
        properties.remove(type);
    }

    public Template getTemplate() {
        return template;
    }

    public ItemStack getStack() {
        return owner;
    }

    public byte[] serialize() {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);) {

            String templateId = template.getId();
            byte[] persistentData = NBT.get(owner, nbt -> {
                if(nbt.hasTag("properties")) {
                    ReadableNBT propertyNbt = nbt.getCompound("properties");
                    return propertyNbt.toString().getBytes(StandardCharsets.UTF_8);
                } else {
                    return new byte[0];
                }
            });
            out.writeUTF(templateId);
            out.writeInt(persistentData.length);
            out.write(persistentData);
            return baos.toByteArray();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return new byte[0];
    }
}
