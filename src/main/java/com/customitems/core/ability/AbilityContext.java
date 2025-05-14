package com.customitems.core.ability;

import com.customitems.core.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public record AbilityContext(Player player, Item item, @Nullable Event event, Ability ability) {

    public <T extends Event> @Nullable T event(Class<T> type) {
        return type.isInstance(event) ? type.cast(event) : null;
    }

    public static AbilityContext of(Player player, Item item, @Nullable Event event, Ability ability) {
        return new AbilityContext(player, item, event, ability);
    }
}
