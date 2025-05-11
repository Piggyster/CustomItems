package com.customitems.core.item;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.template.Template;
import com.customitems.core.property.*;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private Map<Class<? extends Property>, Property> properties;

    public static Item of(ItemStack itemStack) {
        ItemManager itemManager = ItemPlugin.get().getItemManager();
        return itemManager.getItem(itemStack);
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

        load();

        if(mark()) {
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


    /**
     * Loads an item's properties from NBT data as well as
     * the default properties from the template.
     */
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
