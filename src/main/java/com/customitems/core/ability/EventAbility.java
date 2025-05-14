package com.customitems.core.ability;

import org.bukkit.event.Event;

import java.util.Set;

public interface EventAbility extends Ability {

    Set<Class<? extends Event>> getEvents();
}
