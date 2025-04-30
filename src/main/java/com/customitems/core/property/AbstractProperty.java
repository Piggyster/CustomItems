package com.customitems.core.property;

import com.customitems.core.item.CustomItem;

public abstract class AbstractProperty implements Property {

    private CustomItem item;

    protected AbstractProperty() {
        item = null;
    }

    @Override
    public void onAttach(CustomItem item) {
        this.item = item;
    }

    @Override
    public void onDetach(CustomItem item) {
        this.item = null;
    }

    @Override
    public CustomItem getItem() {
        return item;
    }

    @Override
    public String toString() {
        return "Property{" +
                "type='" + getType() + "'}";
    }
}
