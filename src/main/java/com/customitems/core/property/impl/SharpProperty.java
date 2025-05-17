package com.customitems.core.property.impl;

import com.customitems.core.item.Item;
import com.customitems.core.property.*;
import com.customitems.core.stat.StatPhase;
import com.customitems.core.stat.StatProvider;
import com.customitems.core.stat.StatType;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;

import java.util.Map;

public class SharpProperty extends AbstractProperty implements PersistentProperty, StatProvider {

    public static final PropertyType<SharpProperty> TYPE = PropertyType.of(SharpProperty.class, "sharp")
            .json(json -> new SharpProperty())
            .nbt(nbt -> new SharpProperty())
            .build();


    private static final double SHARP_MODIFIER_VALUE = 5;
    //private static final StatModification SHARP_MOD =
            //new StatModification(StatType.DAMAGE, PropertyModification.Operation.ADD, SHARP_MODIFIER_VALUE);


    public SharpProperty() {

    }

    /*
    @Override
    public void init(Item item) {
        super.init(item);
        StatProperty statProperty = item.getProperty(StatProperty.class);
        if(statProperty == null) return;
        statProperty.modify(SHARP_MOD);
    }*/

    @Override
    public PropertyType<SharpProperty> getType() {
        return TYPE;
    }


    @Override
    public boolean load(ReadableNBT nbt) {
        return false;
    }

    @Override
    public void save(ReadWriteNBT nbt) {
        nbt.setByte("sharp", (byte) 1);
    }

    @Override
    public void applyStats(Map<StatType, Double> stats) {
        stats.merge(StatType.DAMAGE, SHARP_MODIFIER_VALUE, Double::sum);
    }

    @Override
    public StatPhase getPhase() {
        return StatPhase.FLAT_ADD;
    }
}
