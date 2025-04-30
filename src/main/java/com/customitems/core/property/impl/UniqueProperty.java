package com.customitems.core.property.impl;

import com.customitems.core.property.AbstractProperty;
import com.customitems.core.property.PersistentProperty;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;

import java.util.UUID;

public class UniqueProperty extends AbstractProperty implements PersistentProperty {

    private UUID uuid;

    public UniqueProperty() {
        uuid = UUID.randomUUID();
    }

    public UniqueProperty(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean loadData(ReadWriteNBT nbt) {
        uuid = nbt.getUUID("uuid");
        if(uuid == null) {
            uuid = UUID.randomUUID();
            saveData(nbt);
        }
        return true;
    }

    @Override
    public boolean saveData(ReadWriteNBT nbt) {
        if(uuid == null) {
            uuid = UUID.randomUUID();
        }
        nbt.setUUID("uuid", uuid);
        return false;
    }

    @Override
    public String getType() {
        return "unique";
    }
}
