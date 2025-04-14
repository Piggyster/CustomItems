package com.customitems.core.property.impl;

import com.customitems.core.property.AbstractProperty;
import com.customitems.core.property.LoreContributor;
import com.customitems.core.property.PropertyModification;
import com.customitems.core.property.ReceiverProperty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeProperty extends AbstractProperty implements LoreContributor, ReceiverProperty<Double> {

    private final AttributeType type;
    private final double baseValue;
    private final Map<String, Double> modifications;

    public AttributeProperty(AttributeType type, double baseValue) {
        this.type = type;
        this.baseValue = baseValue;
        modifications = new ConcurrentHashMap<>();
    }

    public AttributeType getAttributeType() {
        return type;
    }

    @Override
    public void registerModification(PropertyModification<Double> modification) {
        modifications.put(modification.getSource(), modification.getValue());

        if(getItem() != null) {
            getItem().updateItemDisplay();
        }
    }

    @Override
    public boolean removeModification(String sourcePropertyType) {
        boolean removed = modifications.remove(sourcePropertyType) != null;

        if(removed && getItem() != null) {
            getItem().updateItemDisplay();
        }
        return removed;
    }

    public Double getBaseValue() {
        return baseValue;
    }

    @Override
    public Double getEffectiveValue() {
        return baseValue + getModifierOffset();
    }

    private double getModifierOffset() {
        double offset = 0.0;
        for (Double mod : modifications.values()) {
            offset += mod;
        }
        return Math.max(0, offset);
    }


    @Override
    public int getLorePriority() {
        return 1000;
    }

    @Override
    public List<String> contributeLore() {
        switch(type) {
            case DAMAGE -> {
                if(getModifierOffset() == 0) {
                    return List.of("&7Damage: &c+" + getEffectiveValue());
                } else {
                    return List.of("&7Damage: &c+" + getEffectiveValue() + " &9(+" + getModifierOffset() + ")");
                }
            }
            default -> {
                return List.of();
            }
        }
    }

    @Override
    public String getType() {
        return "attribute." + type.getInternalName();
    }

    @Override
    public String toString() {
        return "AttributeProperty{" +
                "type=" + type +
                ", baseValue=" + baseValue +
                ", modifications=" + modifications +
                '}';
    }
}
