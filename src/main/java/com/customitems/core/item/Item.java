package com.customitems.core.item;

import com.customitems.core.attribute.Attribute;
import com.customitems.core.attribute.AttributeFactory;
import com.customitems.core.attribute.AttributeRegistry;
import com.customitems.core.component.Component;
import com.customitems.core.component.impl.CategoryComponent;
import com.customitems.core.handler.RarityHandler;
import com.customitems.core.handler.display.DisplayHandler;
import com.customitems.core.handler.display.DisplayVisitor;
import com.customitems.core.item.template.Template;
import com.customitems.core.service.Services;
import com.google.common.base.Splitter;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
    private List<Attribute<?>> attributes; //TODO map

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
        this.owner = owner.clone();
        this.template = template;
        attributes = new ArrayList<>();

        load();

        //if(mark() || !owner.hasItemMeta()) {
        //    updateDisplay();
        //}
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
        attributes.clear();

        //TODO only get, not modify
        NBT.modify(owner, nbt -> {
            ReadWriteNBT attributeNbt = nbt.hasTag("attributes") ? nbt.getCompound("attributes") : null;

            if(attributeNbt != null) {
                for(AttributeFactory<?> factory : AttributeRegistry.getFactories()) {
                    if(attributeNbt.hasTag(factory.getKey())) {
                        Attribute<?> attribute = factory.newInstance();
                        attribute.loadFromNBT(attributeNbt);
                        attributes.add(attribute);
                    }
                }
            }

            if(attributes.isEmpty()) {
                nbt.removeKey("attributes");
            }
        });
    }


    /**
     * Saves the item's properties to NBT data.
     * Only applies to persistent properties.
     */

    private void save(ItemStack itemStack) {
        NBT.modify(itemStack, nbt -> {
            if(!template.isVanilla() && !nbt.hasTag("item_id")) {
                nbt.setString("item_id", template.getId());
            }

            nbt.removeKey("attributes");

            if(attributes.isEmpty()) return;

            ReadWriteNBT attributeNbt = nbt.getOrCreateCompound("attributes");
            for(Attribute<?> attribute : attributes) {
                attribute.saveToNBT(attributeNbt);
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

        /*List<LoreContributor> loreContributors = properties.values().stream()
                .filter(p -> p instanceof LoreContributor)
                .map(p -> (LoreContributor) p)
                .sorted(Comparator.comparingInt(LoreContributor::getLorePriority).reversed())
                .toList();

        LoreVisitor visitor = new LoreVisitor();

        loreContributors.forEach(contributor -> contributor.contributeLore(visitor));

        visitor.visit("");
        visitor.visit(rarity.getColor().toString() + ChatColor.BOLD + rarity.getDisplayName().toUpperCase());

        meta.setLore(visitor.getLore());
        */


        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        owner.setItemMeta(meta);
        owner.setType(template.getMaterial());
    }

    public ItemStack update(Player player, ItemStack itemStack) {
        template.getComponents().forEach((clazz, component) -> {
            component.updateItem(this);
        });

        ItemMeta meta = itemStack.getItemMeta();
        assert(meta != null);

        ItemRarity rarity = getRarity();

        DisplayVisitor visitor = new DisplayVisitor();

        visitor.setDisplayName(rarity.getColor() + template.getDisplayName());
        visitor.addLore("");

        for(Attribute<?> attribute : attributes) {
            if(attribute instanceof DisplayHandler displayHandler) {
                displayHandler.processDisplay(player, visitor);
            }
        }

        for(Component component : template.getComponents().values()) {
            if(component instanceof DisplayHandler displayHandler) {
                displayHandler.processDisplay(player, visitor);
            }
        }

        String rarityDisplay = rarity.getColor() + "&l" + rarity.getDisplayName().toUpperCase();
        if(hasComponent(CategoryComponent.class)) {
            CategoryComponent component = getComponent(CategoryComponent.class);
            rarityDisplay += " " + component.getCategory().toUpperCase();
        }
        visitor.addLore(rarityDisplay);

        String nbtString = NBT.get(itemStack, nbt -> {
            return nbt.toString();
        });

        visitor.addLore(Splitter.fixedLength(50).splitToList(nbtString));

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', visitor.getDisplayName()));

        List<String> lore = visitor.getLore().stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).toList();
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.values());
        meta.setUnbreakable(true);

        itemStack.setItemMeta(meta);
        itemStack.setType(template.getMaterial());
        save(itemStack);
        return itemStack;
    }


    public <T> boolean hasAttribute(Class<? extends Attribute<T>> clazz) {
        for(Attribute<?> attribute : attributes) {
            if(attribute.getClass().isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> Attribute<T> getAttribute(Class<? extends Attribute<T>> clazz) {
        for(Attribute<?> attribute : attributes) {
            if(attribute.getClass().isAssignableFrom(clazz)) {
                return (Attribute<T>) attribute;
            }
        }
        return null;
    }

    public <T> void addAttribute(Attribute<T> attribute) {
        attributes.add(attribute);
    }

    public <T> void removeAttribute(Class<? extends Attribute<T>> clazz) {
        attributes.removeIf(attribute -> attribute.getClass().isAssignableFrom(clazz));
    }

    public <T> boolean hasComponent(Class<? extends Component> clazz) {
        return template.getComponents().containsKey(clazz);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T getComponent(Class<? extends Component> clazz) {
        return (T) template.getComponents().get(clazz);
    }


    public Template getTemplate() {
        return template;
    }

    public ItemRarity getRarity() {
        //TODO rarity modifiers
        ItemRarity rarity = template.getRarity();

        for(Attribute<?> attribute : attributes) {
            if(attribute instanceof RarityHandler rarityHandler) {
                rarity = rarityHandler.processRarity(this, null);
            }
        }

        for(Component component : template.getComponents().values()) {
            if(component instanceof RarityHandler rarityHandler) {
                rarity = rarityHandler.processRarity(this, null);
            }
        }

        return rarity;
    }

    public ItemStack getStack() {
        return owner;
    }

    public byte[] serialize() {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);) {

            String templateId = template.getId();
            byte[] persistentData = NBT.get(owner, nbt -> {
                if(nbt.hasTag("attributes")) {
                    ReadableNBT attributeNbt = nbt.getCompound("attributes");
                    return attributeNbt.toString().getBytes(StandardCharsets.UTF_8);
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
