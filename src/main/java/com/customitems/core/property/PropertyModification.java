package com.customitems.core.property;

public class PropertyModification<T> {

    private final String propertyType;
    private final T modificationValue;

    public PropertyModification(String propertyType, T modificationValue) {
        this.propertyType = propertyType;
        this.modificationValue = modificationValue;
    }

    public String getSource() {
        return propertyType;
    }

    public T getValue() {
        return modificationValue;
    }
}
