package com.customitems.core.ability.impl;

import com.customitems.core.ability.*;
import com.customitems.core.ability.node.AbilityNode;
import com.customitems.core.ability.node.SequenceNode;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class TeleportAbility implements EventAbility {

    private static final AbilityNode TREE = new SequenceNode()
            .add(new TeleportNode());

    @Override
    public void trigger(AbilityContext context) {
        PlayerInteractEvent event = context.event(PlayerInteractEvent.class);
        if(event == null) return;
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        if(TREE.run(context) == AbilityNode.Result.SUCCESS) {
            context.player().sendMessage("You have been teleported!");
        }
    }

    @Override
    public String getType() {
        return "teleport";
    }

    @Override
    public Set<Class<? extends Event>> getEvents() {
        return Set.of(PlayerInteractEvent.class);
    }
}
