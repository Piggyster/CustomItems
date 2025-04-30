package com.customitems.core.property.ability;

import com.customitems.core.item.CustomItem;

public interface Ability {

    String getAbilityName();
    void activate(CustomItem item);
}
