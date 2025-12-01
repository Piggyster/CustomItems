package com.customitems.core.attribute.impl;

import com.customitems.core.attribute.Attribute;
import com.customitems.core.handler.display.DisplayHandler;
import com.customitems.core.handler.display.DisplayVisitor;
import com.customitems.core.item.Item;
import com.customitems.core.item.ItemManager;
import com.customitems.core.item.template.Template;
import com.customitems.core.service.Services;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class BackpackDataAttribute extends Attribute<BackpackDataAttribute.BackpackData> implements DisplayHandler {

    @Override
    public String getKey() {
        return "backpack";
    }

    @Override
    public BackpackData loadFromNBT(ReadableNBT nbt) {
        byte[] bytes = nbt.getByteArray(getKey());
        if(bytes == null || bytes.length == 0) {
            return new BackpackData(Map.of());
        }

        Map<Integer, Item> items = deserialize(bytes);
        return value = new BackpackData(items);
    }

    @Override
    public void saveToNBT(ReadWriteNBT nbt) {
        if(value == null || value.getContents().isEmpty()) {
            nbt.removeKey(getKey()); //? set empty instead of removing
            return;
        }

        byte[] bytes = serialize(value.getContents());
        nbt.setByteArray(getKey(), bytes);
    }

    @Override
    public void processDisplay(Player player, DisplayVisitor visitor) {
        int quantity = value == null ? 0 : value.getContents().size();
        visitor.setDisplayName(visitor.getDisplayName() + " &7[" + quantity + "]");

        if(quantity == 0) {
            visitor.addLore("&7Contents Empty");
        } else {
            visitor.addLore("&7Contents:");
            quantity = Math.min(6, quantity);

            int i = 0;
            for(Map.Entry<Integer, Item> entry : value.getContents().entrySet()) {
                Item item = entry.getValue();
                item.update(player, item.getStack());
                String displayName = item.getStack().getItemMeta().getDisplayName();
                visitor.addLore(" &o" + displayName);

                i++;
                if(quantity == i) break;
            }

            if(value.getContents().size() > quantity) {
                visitor.addLore(" &8...and " + (value.getContents().size() - quantity) + " more");
            }
        }
    }

    public static class BackpackData {
        private Map<Integer, Item> contents;

        public BackpackData(Map<Integer, Item> contents) {
            this.contents = contents;
        }

        public Map<Integer, Item> getContents() {
            return contents;
        }
    }

    private static byte[] serialize(Map<Integer, Item> items) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);) {
            for(Map.Entry<Integer, Item> entry : items.entrySet()) {
                int slot = entry.getKey();
                Item item = entry.getValue();
                String templateId = item.getTemplate().getId();
                int amount = item.getStack().getAmount();

                byte[] persistentData = NBT.get(item.getStack(), nbt -> {
                    if(nbt.hasTag("attributes")) {
                        ReadableNBT attributeNbt = nbt.getCompound("attributes");
                        return attributeNbt.toString().getBytes(StandardCharsets.UTF_8);
                    } else {
                        return null;
                    }
                });

                if(persistentData == null) persistentData = new byte[0];

                out.writeInt(slot);
                out.writeUTF(templateId);
                out.writeInt(persistentData.length);
                out.write(persistentData);
                out.writeInt(amount);
            }
            return baos.toByteArray();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return new byte[0];
    }

    private static Map<Integer, Item> deserialize(byte[] data) {
        ItemManager itemManager = Services.get(ItemManager.class);
        Map<Integer, Item> items = new HashMap<>();
        try(DataInputStream in = new DataInputStream(new ByteArrayInputStream(data))) {
            while(in.available() > 0) {
                int slot = in.readInt();
                String templateId = in.readUTF();
                int persistentDataLength = in.readInt();
                byte[] persistentData = in.readNBytes(persistentDataLength);
                int amount = in.readInt();

                Template template = itemManager.getTemplate(templateId);
                if(template == null) continue;

                ItemStack stack = template.createItemStack();
                if(persistentDataLength > 0) {
                    NBT.modify(stack, nbt -> {
                        ReadWriteNBT attributeNbt = nbt.getOrCreateCompound("attributes");
                        attributeNbt.mergeCompound(NBT.parseNBT(new String(persistentData, StandardCharsets.UTF_8)));
                    });
                }
                stack.setAmount(amount);

                Item item = new Item(stack, template);
                items.put(slot, item);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return items;
    }
}
