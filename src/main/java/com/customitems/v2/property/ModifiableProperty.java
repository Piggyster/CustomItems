package com.customitems.v2.property;

public interface ModifiableProperty<T extends PropertyModification> extends Property {

    void modify(T mod);

}
