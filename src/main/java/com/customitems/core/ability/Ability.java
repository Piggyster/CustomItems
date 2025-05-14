package com.customitems.core.ability;


public interface Ability {

    void trigger(AbilityContext context);

    String getType();
}
