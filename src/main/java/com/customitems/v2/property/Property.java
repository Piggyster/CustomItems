package com.customitems.v2.property;

import com.customitems.v2.item.Item;

public interface Property {

    void init(Item item);

    String getType();

    Item getItem();

    PropertyPriority getPriority();

}
