package com.customitems.core.property.impl;

import com.customitems.core.item.Item;
import com.customitems.core.property.AbstractProperty;
import com.customitems.core.property.PersistentProperty;
import com.customitems.core.property.PropertyModification;
import com.customitems.core.property.PropertyPriority;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;

public class SharpProperty extends AbstractProperty implements PersistentProperty {

    private static final int SHARP_MODIFIER_VALUE = 5;
    private static final StatModification SHARP_MOD =
            new StatModification(StatType.DAMAGE, PropertyModification.Operation.ADD, SHARP_MODIFIER_VALUE);


    public SharpProperty() {

    }

    @Override
    public void init(Item item) {
        super.init(item);
        StatProperty statProperty = item.getProperty(StatProperty.class);
        if(statProperty == null) return;
        statProperty.modify(SHARP_MOD);
    }

    @Override
    public String getType() {
        return "sharp";
    }

    @Override
    public PropertyPriority getPriority() {
        return PropertyPriority.INTER;
    }

    @Override
    public void load(ReadableNBT nbt) {

    }

    @Override
    public void save(ReadWriteNBT nbt) {
        nbt.setByte("sharp", (byte) 1);
    }
}
