package com.customitems.core.property;

import org.bukkit.event.Event;

public interface EventListener<T extends Event> {

    Class<T> getEventClass();

    void handleEvent(T event);
}
