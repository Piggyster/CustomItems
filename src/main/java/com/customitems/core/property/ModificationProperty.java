package com.customitems.core.property;

import com.customitems.core.item.CustomItem;

public interface ModificationProperty<T> extends Property {

    String getTargetPropertyType();

    T getModificationValue();

    @SuppressWarnings("unchecked")
    default void applyModification() {
        CustomItem item = getItem();
        if(item == null) {
            return;
        }

        String targetType = getTargetPropertyType();
        Property target = item.getProperty(targetType);
        if(target instanceof ReceiverProperty) {
            PropertyModification<T> modification = new PropertyModification<>(getType(), getModificationValue());
            ((ReceiverProperty<T>) target).registerModification(modification);
        }
    }

    @SuppressWarnings("unchecked")
    default void removeModification() {
        CustomItem item = getItem();
        if(item == null) {
            return;
        }

        String targetType = getTargetPropertyType();
        Property target = item.getProperty(targetType);
        if(target instanceof ReceiverProperty) {
            ((ReceiverProperty<T>) target).removeModification(getType());
        }

    }
}
