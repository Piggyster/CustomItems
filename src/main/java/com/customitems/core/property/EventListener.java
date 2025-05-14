package com.customitems.core.property;

import org.bukkit.event.Event;

import java.util.Set;

public interface EventListener {

    void handle(Event event);

    Set<Class<? extends Event>> getEvents();
}
