package com.customitems.core.ability;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityRegistry {

    private final Map<String, Ability> abilities;

    public AbilityRegistry() {
        abilities = new ConcurrentHashMap<>();
    }

    public void register(Ability ability) {
        abilities.put(ability.getType(), ability);
    }

    public @Nullable Ability getAbility(String type) {
        return abilities.get(type);
    }
}
