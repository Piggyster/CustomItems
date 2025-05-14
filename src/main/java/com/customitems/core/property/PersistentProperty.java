package com.customitems.core.property;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;

/**
 * Represents a property of an item that can be persisted.
 * <p>
 * Persistent properties are an extension to Property that can be
 * loaded and saved into an item's NBT data.
 * </p>
 */

public interface PersistentProperty extends Property {
    //TODO maybe work on treating it more like a supplier
    boolean load(ReadableNBT nbt);
    void save(ReadWriteNBT nbt);
}
