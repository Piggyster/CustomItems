package com.customitems.core.property;

import com.customitems.core.item.Item;

/**
 * Abstract class for properties.
 * This class implements the Property interface and provides a default implementation for the init method.
 * It also provides a method to get the item associated with the property.
 */

public abstract class AbstractProperty implements Property {

    private Item item;

    public AbstractProperty() {
        item = null;
    }

    @Override
    public void init(Item item) {
        this.item = item;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "Property{" +
                "type='" + getType() + '\'' +
                '}';
    }
}
