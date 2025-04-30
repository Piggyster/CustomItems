package com.customitems.core.property.ability;

import com.customitems.core.item.CustomItem;
import com.customitems.core.property.AbstractProperty;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class AbilityProperty<E extends Event> extends AbstractProperty implements EventListenerProperty<E>, Ability {

    private final AbilityTrigger trigger;

    public AbilityProperty(AbilityTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    public void handleEvent(E event, Player player, CustomItem item) {
    }

    @Override
    public String getType() {
        return "ability." + getAbilityName();
    }

}
