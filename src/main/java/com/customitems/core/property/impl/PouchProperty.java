package com.customitems.core.property.impl;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.Item;
import com.customitems.core.item.ItemManager;
import com.customitems.core.item.template.Template;
import com.customitems.core.item.template.loader.TemplateLoader;
import com.customitems.core.property.*;
import com.customitems.core.service.Services;
import com.google.common.collect.Maps;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PouchProperty extends AbstractProperty implements PersistentProperty, LoreContributor, EventListener, MergeableProperty {

    public static final PropertyType<PouchProperty> TYPE = PropertyType.of(PouchProperty.class, "pouch")
            .json(json -> new PouchProperty(json.getAsInt()))
            .nbt(nbt -> {
                PouchProperty property = new PouchProperty(Maps.newConcurrentMap());
                property.load(nbt);
                return property;
            })
            .build();

    private Inventory inventory;
    private Map<Integer, Item> contents;
    private int size;

    public PouchProperty(int size) {
        this.size = size;
    }

    public PouchProperty(Map<Integer, Item> contents) {
        this.contents = contents;
    }

    @Override
    public int getLorePriority() {
        return 1;
    }

    @Override
    public void contributeLore(LoreVisitor visitor) {
        visitor.visit("&ePouch Slots: " + (size * 9));
        visitor.visit("");
        if(contents.isEmpty()) {
            visitor.visit("&7Empty pouch...");
        } else {
            visitor.visit("&7Pouch containing:");
            int count = Math.min(contents.size(), 6);
            for(int i = 0; i < count; i++) {
                Item item = contents.get(i);
                if(item != null) {
                    visitor.visit(" &o" + item.getStack().getItemMeta().getDisplayName());
                }
            }
            if(contents.size() > count) {
                visitor.visit(" &8...and " + (contents.size() - count) + " more");
            }
        }
    }

    @Override
    public boolean load(ReadableNBT nbt) {
        if(!nbt.hasTag("pouch")) {
            contents = new ConcurrentHashMap<>();
            return true;
        } else {
            contents = deserialize(nbt.getByteArray("pouch"));
            return false;
        }
    }

    public Inventory getInventory() {
        if(inventory == null) {
            inventory = Bukkit.getServer().createInventory(null, size * 9, "Item Pouch");
            for(Map.Entry<Integer, Item> entry : contents.entrySet()) {
                int slot = entry.getKey();
                Item item = entry.getValue();
                if(item != null) {
                    inventory.setItem(slot, item.getStack());
                }
            }
        }
        return inventory;
    }

    public int getSize() {
        return size;
    }

    public void updateContents() {
        contents.clear();
        for(ItemStack stack : inventory.getContents()) {
            if(stack == null || stack.getType().isAir()) continue;
            Item item = Item.of(stack);
            item.updateDisplay();
            contents.put(inventory.first(stack), item);
        }
    }

    @Override
    public void save(ReadWriteNBT nbt) {
        nbt.setByteArray("pouch", serialize(contents));
    }

    @Override
    public PropertyType<PouchProperty> getType() {
        return TYPE;
    }

    public static byte[] serialize(Map<Integer, Item> items) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);) {
            for(Map.Entry<Integer, Item> entry : items.entrySet()) {
                int slot = entry.getKey();
                Item item = entry.getValue();
                String templateId = item.getTemplate().getId();
                item.save();
                byte[] persistentData = NBT.get(item.getStack(), nbt -> {
                    if(nbt.hasTag("properties")) {
                        ReadableNBT propertyNbt = nbt.getCompound("properties");
                        return propertyNbt.toString().getBytes(StandardCharsets.UTF_8);
                    } else {
                        return null;
                    }
                });

                if(persistentData == null) persistentData = new byte[0];

                out.writeInt(slot);
                out.writeUTF(templateId);
                out.writeInt(persistentData.length);
                out.write(persistentData);
            }
            return baos.toByteArray();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return new byte[0];
    }

    public static Map<Integer, Item> deserialize(byte[] data) {
        ItemManager itemManager = Services.get(ItemManager.class);
        Map<Integer, Item> items = new HashMap<>();
        try(DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
            while(in.available() > 0) {
                int slot = in.readInt();
                String templateId = in.readUTF();
                int persistentDataLength = in.readInt();
                byte[] persistentData = in.readNBytes(persistentDataLength);

                Template template = itemManager.getTemplate(templateId);
                if(template == null) continue;

                ItemStack stack = template.createItemStack();
                if(persistentDataLength > 0) {
                    NBT.modify(stack, nbt -> {
                        ReadWriteNBT propertyNbt = nbt.getOrCreateCompound("properties");
                        propertyNbt.mergeCompound(NBT.parseNBT(new String(persistentData, StandardCharsets.UTF_8)));
                    });
                }

                Item item = new Item(stack, template);
                items.put(slot, item);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return items;
    }

    @Override
    public void handle(Event genericEvent) {
        if(genericEvent instanceof PlayerInteractEvent event) {
            if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
            event.getPlayer().openInventory(getInventory());
            event.setCancelled(true);
        } else if(genericEvent instanceof InventoryCloseEvent event) {
            if(!event.getInventory().equals(getInventory())) return;
            updateContents();
            getItem().save();
            getItem().updateDisplay();
        } else if(genericEvent instanceof InventoryClickEvent event) {
            if(!getInventory().equals(event.getInventory())) return;
            ItemStack stack = event.getCurrentItem();
            if(stack == null || stack.getType().isAir()) return;
            Item item = Item.of(stack);
            if(item.getTemplate().isVanilla()) return;
            if(!item.hasProperty(PouchProperty.TYPE)) return;
            event.setCancelled(true);
            //event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot put a pouch inside another pouch!");
        }
    }

    @Override
    public Set<Class<? extends Event>> getEvents() {
        return Set.of(PlayerInteractEvent.class, InventoryCloseEvent.class, InventoryClickEvent.class);
    }

    @Override
    public void merge(Property property) {
        if(!(property instanceof PouchProperty pouchProperty)) return;
        size = pouchProperty.size;
    }
}
