package com.customitems.core.property;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;


public interface PersistentProperty extends Property {

    default String getTypeKey() {
        return getType();
    }

    boolean loadData(ReadWriteNBT nbt);

    boolean saveData(ReadWriteNBT nbt);
}
