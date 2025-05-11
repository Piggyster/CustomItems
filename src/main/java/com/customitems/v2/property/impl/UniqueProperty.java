package com.customitems.v2.property.impl;

import com.customitems.v2.property.AbstractProperty;
import com.customitems.v2.property.PersistentProperty;
import com.customitems.v2.property.PropertyPriority;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;

import java.util.UUID;

public class UniqueProperty extends AbstractProperty implements PersistentProperty {

    private UUID uuid;
    private long timestamp;

    //TODO source of item

    public UniqueProperty() {

    }

    public UniqueProperty(UUID uuid, long timestamp) {
        this.uuid = uuid;
        this.timestamp = timestamp;
    }

    @Override
    public void load(ReadableNBT nbt) {
        uuid = nbt.getUUID("uuid");
        timestamp = nbt.getLong("timestamp");
    }

    @Override
    public void save(ReadWriteNBT nbt) {
        nbt.setUUID("uuid", uuid);
        nbt.setLong("timestamp", timestamp);
    }

    @Override
    public String getType() {
        return "uuid";
    }

    @Override
    public PropertyPriority getPriority() {
        return PropertyPriority.MASTER;
    }
}
