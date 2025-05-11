package com.customitems.core.property;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;


public interface PersistentProperty extends Property {

    boolean loadData(ReadableNBT nbt);

    boolean saveData(ReadWriteNBT nbt);
}
