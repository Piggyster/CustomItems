package com.customitems.core.attribute;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public abstract class Attribute<T> {


    protected T value;

    public Attribute() {
        value = getDefaultValue();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract String getKey();

    public T getDefaultValue() {
        return null;
    }

    public abstract T loadFromNBT(ReadableNBT nbt);

    public abstract void saveToNBT(ReadWriteNBT nbt);
}
