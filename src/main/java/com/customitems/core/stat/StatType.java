package com.customitems.core.stat;

public enum StatType {
    DAMAGE(1.0),
    DEFENCE(0.0),
    MANA(50.0);

    private final double baseValue;

    StatType(double baseValue) {
        this.baseValue = baseValue;
    }

    public double getBaseValue() {
        return baseValue;
    }
}