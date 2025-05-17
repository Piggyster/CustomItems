package com.customitems.core.property;

import com.customitems.core.item.Item;

/**
 * Represents a property of an item.
 * <p>
 * Properties are used to define the behavior and characteristics of items.
 * </p>
 */

public interface Property {

    void init(Item item);

    PropertyType<? extends Property> getType();

    Item getItem();

}
