package com.customitems.core.item;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.property.*;
import com.customitems.core.property.EventListener;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class CustomItem {

    public static boolean isCustomItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }

        return NBT.get(itemStack, nbt -> {
            return nbt.hasTag("item_type");
        });
    }


    private ItemStack itemStack;
    private final ItemTemplate template;
    private final Map<String, Property> properties;
    private final List<ModificationProperty<?>> modificationProperties;


    public CustomItem(ItemTemplate template) {
        this(template.createItemStack(), template);
    }

    public CustomItem(ItemStack itemStack, ItemTemplate template) {
        this.itemStack = itemStack;
        this.template = template;
        properties = new ConcurrentHashMap<>();
        modificationProperties = new ArrayList<>();

        markAsCustomItem(); //checks for meta to avoid vanilla items

        loadPropertiesFromItem();

        for(Property property : template.getDefaultProperties()) {
            if(!hasProperty(property.getType())) {
                addProperty(property);
            }
        }

        updateItemDisplay(); //could be redundant since we update display in addProperty. keep for now since sometimes no properties are added
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void handleEvent(T event) {
        for(Property property : properties.values()) {
            if(property instanceof EventListener<?>) {
                EventListener<Event> listener = (EventListener<Event>) property;
                if(listener.getEventClass().isInstance(event)) {
                    listener.handleEvent(event);
                }
            }
        }
    }

    public void savePropertiesToItem() {
        //save from map to NBT

        //possibly add checks to avoid saving if nothing has changed

        NBT.modify(itemStack, nbt -> {

            if(properties.isEmpty() && nbt.hasTag("properties")) { //simple check if there is no properties and tag exists, remove it
                nbt.removeKey("properties");
                return;
            }

            ReadWriteNBTCompoundList properties = nbt.getCompoundList("properties");
            properties.clear();

            boolean executed = false;

            for(Property property : this.properties.values()) {
                if(property instanceof PersistentProperty persistentProperty) {
                    ReadWriteNBT propertyNbt = properties.addCompound();
                    persistentProperty.saveData(propertyNbt);
                    executed = true;
                }
            }

            if(!executed) { //deeper check if properties are empty after operation
                nbt.removeKey("properties");
            }
        });
    }


    private void loadPropertiesFromItem() {
        NBT.get(itemStack, nbt -> {
            if(nbt.hasTag("properties")) {
                ReadableNBTList<ReadWriteNBT> properties = nbt.getCompoundList("properties");
                for(ReadWriteNBT propertyNbt : properties) {
                    String propertyType = propertyNbt.getString("type");
                    if(PropertyRegistry.hasFactory(propertyType)) {
                        Property property = PropertyRegistry.createProperty(propertyType, propertyNbt);
                        if(property != null) {
                            addProperty(property);
                        }
                    } else {
                        //find in template if property does not have a factory
                        for(Property templateProperty : template.getDefaultProperties()) { //loop through default template properties
                            if(templateProperty instanceof PersistentProperty persistentProperty && templateProperty.getType().equals(propertyType)) { //check the types for a match
                                if(persistentProperty.loadData(propertyNbt)) { //load data from container into the property
                                    addProperty(templateProperty);
                                    break;
                                }
                            }
                        }
                    }
                }
            }


        });
    }


    private void markAsCustomItem() {
        if(template instanceof VanillaItemTemplate) return;

        NBT.modify(itemStack, nbt -> {
            nbt.setString("item_type", template.getId());
        });
    }

    public void updateItemDisplay() {
        ItemMeta meta = itemStack.getItemMeta();
        if(meta == null) return;

        ItemRarity rarity = template.getRarity();

        meta.setDisplayName(rarity.getColor() + template.getDisplayName());

        List<LoreContributor> loreContributors = properties.values().stream()
                .filter(p -> p instanceof LoreContributor)
                .map(p -> (LoreContributor) p)
                .sorted(Comparator.comparingInt(LoreContributor::getLorePriority).reversed())
                .toList();

        List<String> lore = new ArrayList<>();

        for(LoreContributor contributor : loreContributors) {
            lore.addAll(contributor.contributeLore());
        }

        lore.add("");
        lore.add(rarity.getColor() + "&l" + rarity.getDisplay().toUpperCase());

        lore = lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).toList();

        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        itemStack.setItemMeta(meta);
        itemStack.setType(template.getMaterial());
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemTemplate getTemplate() {
        return template;
    }

    public Collection<Property> getProperties() {
        return properties.values();
    }

    @SuppressWarnings("unchecked")
    public <T extends Property> T getProperty(String propertyType, Class<T> clazz) {
        Property property = properties.get(propertyType);
        if (clazz.isInstance(property)) {
            return (T) property;
        }
        return null;
    }

    public Property getProperty(String propertyType) {
        return properties.get(propertyType);
    }

    public boolean addProperty(Property property) {
        if(hasProperty(property.getType())) {
            return false;
        }
        properties.put(property.getType(), property);
        property.onAttach(this);
        property.onItemCreated(this);

        if(property instanceof ModificationProperty<?>) {
            modificationProperties.add((ModificationProperty<?>) property);
        }

        for(ModificationProperty<?> modificationProperty : modificationProperties) {
            if(property.getType().equals(modificationProperty.getTargetPropertyType())) {
                modificationProperty.applyModification();
            }
        }

        updateItemDisplay();
        return true;
    }

    public boolean hasProperty(String propertyType) {
        return properties.containsKey(propertyType);
    }

    public Property removeProperty(String propertyType) {
        Property property = properties.remove(propertyType);
        if (property != null) {
            property.onDetach(this);
            updateItemDisplay();
        }
        return property;
    }

}
