package com.customitems.core.property.impl;

import com.customitems.core.property.*;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;

import java.util.UUID;

public class UniqueProperty extends AbstractProperty implements PersistentProperty, LoreContributor {

    public final static PropertyType<UniqueProperty> TYPE = PropertyType.of(UniqueProperty.class, "uuid")
            .json(json -> new UniqueProperty())
            .nbt(nbt -> {
                UniqueProperty property = new UniqueProperty();
                property.load(nbt);
                return property;
            })
            .build();

    private UUID uuid;
    private long timestamp;

    //TODO source of item

    public UniqueProperty() {

    }

    public UniqueProperty(UUID uuid, long timestamp) {
        this.uuid = uuid;
        this.timestamp = timestamp;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean load(ReadableNBT nbt) {
        if(!nbt.hasTag("uuid")) {
            uuid = UUID.randomUUID();
            timestamp = System.currentTimeMillis();
            return true;
        } else {
            uuid = nbt.getUUID("uuid");
            timestamp = nbt.getLong("timestamp");
            return false;
        }
    }

    @Override
    public void save(ReadWriteNBT nbt) {
        nbt.setUUID("uuid", uuid);
        nbt.setLong("timestamp", timestamp);
    }

    @Override
    public PropertyType<UniqueProperty> getType() {
        return TYPE;
    }


    @Override
    public int getLorePriority() {
        return 1;
    }

    @Override
    public void contributeLore(LoreVisitor visitor) {
        visitor.visit("&8" + uuid.toString());
    }
}
