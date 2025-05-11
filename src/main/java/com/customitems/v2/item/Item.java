package com.customitems.v2.item;

import com.customitems.v2.ItemPlugin;
import com.customitems.v2.property.PersistentProperty;
import com.customitems.v2.property.Property;
import com.customitems.v2.property.PropertyRegistry;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Item {

    private Template template;
    private ItemStack owner;
    private Map<Class<? extends Property>, Property> properties;

    public Item(@NotNull ItemTemplate template) {
        this(template.createItemStack(), template);
    }

    public Item(@NotNull ItemStack owner, @NotNull ItemTemplate template) {
        Objects.requireNonNull(owner);
        Objects.requireNonNull(template);
        this.owner = owner;
        this.template = template;
        properties = new ConcurrentHashMap<>();

        load();

        if(mark()) {
            updateDisplay();
        }
    }

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

    public void load() {
        properties.clear();

        List<Property> rawProperties = new ArrayList<>();

        //load properties from nbt
        NBT.modify(owner, nbt -> {
            if(!nbt.hasTag("properties")) {
                return;
            }

            PropertyRegistry registry = ItemPlugin.get().getRegistry();
            ReadWriteNBT propertyNbt = nbt.getCompound("properties"); //maybe we should be passing through nbt to each factory?
            for(String propertyType : propertyNbt.getKeys()) {
                Class<? extends Property> clazz = registry.getClass(propertyType);
                if(clazz == null) continue;
                Property property = registry.fromNbt(clazz, propertyNbt);
                rawProperties.add(property);
            }

            for(Property property : template.getDefaultProperties()) {
                if(property instanceof PersistentProperty persistentProperty) {
                    persistentProperty.load(propertyNbt);
                    rawProperties.add(persistentProperty);
                } else {
                    rawProperties.add(property);
                }
            }

            if(rawProperties.isEmpty()) {
                nbt.removeKey("properties");
            }
        });

        rawProperties.sort(Comparator.comparingInt(p -> p.getPriority().ordinal())); //TODO
        rawProperties.forEach(this::addProperty);
    }

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

    public void updateDisplay() {
        ItemMeta meta = owner.getItemMeta();
        if(meta == null) return;
        //do meta operations


        owner.setItemMeta(meta);
    }

    public boolean addProperty(@NotNull Property property) {
        Class<? extends Property> clazz = property.getClass();
        if(hasProperty(clazz)) {
            return false;
        }
        properties.put(clazz, property);
        property.init(this);
        return true;
    }

    public boolean hasProperty(Class<? extends Property> clazz) {
        return properties.containsKey(clazz);
    }

    public boolean hasProperty(Property property) {
        return properties.containsValue(property);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Property> T getProperty(Class<? extends T> clazz) {
        return (T) properties.get(clazz);
    }

    public void removeProperty(Class<? extends Property> clazz) {
        properties.remove(clazz);
    }
}
