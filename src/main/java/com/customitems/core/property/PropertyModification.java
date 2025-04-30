package com.customitems.core.property;

public class PropertyModification<T> {

    private final String propertyType;
    private final T modificationValue;
    private final ModificationType modificationType;

    public PropertyModification(String propertyType, T modificationValue, ModificationType modificationType) {
        this.propertyType = propertyType;
        this.modificationValue = modificationValue;
        this.modificationType = modificationType;
    }

    public String getSource() {
        return propertyType;
    }

    public T getValue() {
        return modificationValue;
    }

    public ModificationType getType() {
        return modificationType;
    }

    public enum ModificationType {
        ADDITION,
        MULTIPLICATION,
        SUBTRACTION,
        DIVISION,
        REPLACEMENT
    }
}
