package com.customitems.core.property;

public interface ReceiverProperty<T> extends Property {

    void registerModification(PropertyModification<T> modification);

    boolean removeModification(String sourcePropertyType);

    T getEffectiveValue();
}
