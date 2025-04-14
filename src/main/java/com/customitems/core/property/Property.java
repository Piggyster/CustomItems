package com.customitems.core.property;

import com.customitems.core.item.CustomItem;
import com.google.gson.JsonObject;

import java.util.function.Function;

public interface Property {

    String getType();

    void onAttach(CustomItem item);

    void onDetach(CustomItem item);

    void onItemCreated(CustomItem item);

    CustomItem getItem();
}
