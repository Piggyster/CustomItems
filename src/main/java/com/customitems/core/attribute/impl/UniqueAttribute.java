package com.customitems.core.attribute.impl;

import com.customitems.core.attribute.Attribute;
import com.customitems.core.handler.LoreHandler;
import com.customitems.core.item.Item;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class UniqueAttribute extends Attribute<UUID> implements LoreHandler {

    @Override
    public String getKey() {
        return "unique";
    }

    @Override
    public UUID loadFromNBT(ReadableNBT nbt) {
        value = nbt.getUUID(getKey());
        return value;
    }

    @Override
    public void saveToNBT(ReadWriteNBT nbt) {
        if(value == null) {
            nbt.removeKey(getKey());
        } else {
            nbt.setUUID(getKey(), value);
        }
    }

    @Override
    public List<String> contributeLore(Item item, Player player) {
        return List.of("&eUUID: " + value);
    }
}
