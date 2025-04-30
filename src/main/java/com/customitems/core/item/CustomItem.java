package com.customitems.core.item;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.property.*;
import com.customitems.core.property.ability.EventListenerProperty;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class CustomItem implements Serializable {


    public static CustomItem of(ItemStack itemStack) {
        return CustomItemsPlugin.getInstance().getItemManager().getCustomItem(itemStack);
    }

    private ItemStack itemStack;
    private final ItemTemplate template;
    private final Map<String, Property> properties;
    private final List<ModificationProperty<?>> modificationProperties;
    private final List<EventListenerProperty<?>> listenerProperties;
    private boolean dirty;


    public CustomItem(ItemTemplate template) {
        this(template.createItemStack(), template);
    }

    public CustomItem(ItemStack itemStack, ItemTemplate template) {
        this.itemStack = itemStack; //add check for null itemstack
        this.template = template; //add check for null template
        properties = new ConcurrentHashMap<>();
        modificationProperties = new ArrayList<>();
        listenerProperties = new ArrayList<>();

        if(template instanceof VanillaItemTemplate) {
            if(!itemStack.hasItemMeta()) {
                updateItemDisplay();
            }
            return;
        }

        boolean marked = markAsCustomItem();

        loadPropertiesFromItem();

        for(Property property : template.getDefaultProperties()) {
            if(!hasProperty(property.getType())) {
                addProperty(property);
            }
        }

        NBT.get(itemStack, nbt -> {
            dirty = nbt.hasTag("dirty");
        });

        if(dirty || marked) {
            updateItemDisplay();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void handleEvent(T event) {
        for(EventListenerProperty<?> listener : listenerProperties) {
            if(listener.getEventClass().isInstance(event)) {
                listener.handleEvent(event);
            }
        }

    }

    public boolean hasEventHandler() {
        return !listenerProperties.isEmpty();
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
                    Property property = PropertyRegistry.fromNbt(propertyType, propertyNbt);

                    if(property != null) {
                        addProperty(property);
                    } else {
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

    public void markDirty() {
        NBT.modify(itemStack, nbt -> {
            nbt.setBoolean("dirty", true);
        });
        dirty = true;
    }

    public void markClean() {
        NBT.modify(itemStack, nbt -> {
            nbt.removeKey("dirty");
        });
        dirty = false;
    }


    private boolean markAsCustomItem() {
        if(template instanceof VanillaItemTemplate) return false;
        return NBT.modify(itemStack, nbt -> {
            if(!nbt.hasTag("item_type")) {
                nbt.setString("item_type", template.getId());
                return true;
            } else {
                return false;
            }
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

        itemStack.setItemMeta(itemStack.getItemMeta());

        markClean();
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

        if(property instanceof ModificationProperty<?>) {
            modificationProperties.add((ModificationProperty<?>) property);
        }

        for(ModificationProperty<?> modificationProperty : modificationProperties) {
            if(property.getType().equals(modificationProperty.getTargetPropertyType())) {
                modificationProperty.applyModification();
            }
        } //todo relook this for proper calculation

        if(property instanceof EventListenerProperty<?>) {
            listenerProperties.add(((EventListenerProperty<?>) property));
        }
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
