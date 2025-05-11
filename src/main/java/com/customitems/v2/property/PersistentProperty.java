package com.customitems.v2.property;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;

public interface PersistentProperty extends Property {
    //TODO maybe work on treating it more like a supplier
    void load(ReadableNBT nbt);
    void save(ReadWriteNBT nbt);
}
