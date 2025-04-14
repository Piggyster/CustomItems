package com.customitems.core.property.impl;

public enum AttributeType {
    DAMAGE("damage", "Damage");

    private final String internalName;
    private final String displayName;

    AttributeType(String internalName, String displayName) {
        this.internalName = internalName;
        this.displayName = displayName;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AttributeType fromInternalName(String internalName) {
        if (internalName == null) return null;

        for (AttributeType type : values()) {
            if (type.getInternalName().equalsIgnoreCase(internalName)) {
                return type;
            }
        }
        return null;
    }

}
