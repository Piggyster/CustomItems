package com.customitems.v2.property;

import com.customitems.v2.item.Item;

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
}
