package com.customitems.core.item;

import com.customitems.core.attribute.Attribute;
import com.customitems.core.attribute.AttributeFactory;
import com.customitems.core.attribute.AttributeRegistry;
import com.customitems.core.component.Component;
import com.customitems.core.handler.LoreHandler;
import com.customitems.core.item.template.Template;
import com.customitems.core.service.Services;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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

            if(attributes.isEmpty() && nbt.hasTag("attributes")) {
                nbt.removeKey("attributes");
                return;
            }
            ReadWriteNBT attributeNbt = nbt.getOrCreateCompound("attributes");
            boolean executed = false;
            for(Attribute<?> attribute : attributes) {
                attribute.saveToNBT(attributeNbt);
                executed = true;
            }

            if(!executed) {
                nbt.removeKey("attributes");
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
        meta.setDisplayName(rarity.getColor() + template.getDisplayName());

        List<String> lore = new ArrayList<>();
        lore.add("");

        for(Attribute<?> attribute : attributes) {
            if(attribute instanceof LoreHandler contributor) {
                lore.addAll(contributor.contributeLore(this, player));
            }
        }

        for(Component component : template.getComponents().values()) {
            if(component instanceof LoreHandler contributor) {
                lore.addAll(contributor.contributeLore(this, player));
            }
        }

        lore.add(rarity.getColor() + "&l" + rarity.getDisplayName().toUpperCase());

        String nbtString = NBT.get(itemStack, nbt -> {
            return nbt.toString();
        });

        lore.add("&c" + nbtString);

        lore = lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).toList();
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
        return template.getRarity();
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
