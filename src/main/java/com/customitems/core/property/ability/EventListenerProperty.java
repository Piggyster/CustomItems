package com.customitems.core.property.ability;

import com.customitems.core.item.CustomItem;
import com.customitems.core.property.Property;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface EventListenerProperty<T extends Event> extends Property {

    Class<T> getEventClass();

    void handleEvent(T event, Player player, CustomItem item);
}
