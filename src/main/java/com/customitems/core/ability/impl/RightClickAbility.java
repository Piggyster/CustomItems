package com.customitems.core.ability.impl;

import com.customitems.core.ability.Ability;
import com.customitems.core.ability.AbilityContext;
import com.customitems.core.ability.EventAbility;
import com.customitems.core.ability.node.AbilityNode;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class RightClickAbility implements EventAbility {

    private final String type;
    private final AbilityNode node;
    private final Consumer<AbilityContext> onSuccess;

    public RightClickAbility(String type, AbilityNode node) {
        this(type, node, null);
    }

    public RightClickAbility(String type, AbilityNode node, Consumer<AbilityContext> onSuccess) {
        this.type = type;
        this.node = node;
        this.onSuccess = onSuccess;
    }

    @Override
    public void trigger(AbilityContext context) {
        PlayerInteractEvent event = context.event(PlayerInteractEvent.class);
        if(event == null) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        if(node.run(context) == AbilityNode.Result.SUCCESS && onSuccess != null) {
            onSuccess.accept(context);
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Set<Class<? extends Event>> getEvents() {
        return Set.of(PlayerInteractEvent.class);
    }
}
