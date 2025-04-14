package com.customitems.core.property.impl;

import com.customitems.core.item.CustomItem;
import com.customitems.core.property.AbstractProperty;
import com.customitems.core.property.LoreContributor;
import com.customitems.core.property.ModificationProperty;
import com.customitems.core.property.PersistentProperty;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.NamespacedKey;

import java.util.List;

public class SharpProperty extends AbstractProperty implements ModificationProperty<Double>, PersistentProperty, LoreContributor {

    public SharpProperty() {}

    @Override
    public void onAttach(CustomItem item) {
        super.onAttach(item);
        applyModification();
    }

    @Override
    public void onDetach(CustomItem item) {
        super.onDetach(item);
        removeModification();
    }

    @Override
    public String getTargetPropertyType() {
        return "attribute.damage";
    }

    @Override
    public Double getModificationValue() {
        return 5D;
    }

    @Override
    public String getType() {
        return "sharp";
    }

    @Override
    public boolean loadData(ReadWriteNBT nbt) {
        return true;
    }

    @Override
    public boolean saveData(ReadWriteNBT nbt) {
        nbt.setString("type", getType());
        return true;
    }


    @Override
    public List<String> contributeLore() {
        return List.of("&8Sharp");
    }
}
