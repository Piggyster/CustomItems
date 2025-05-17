package com.customitems.core.ability.impl;

import com.customitems.core.ability.AbilityContext;
import com.customitems.core.ability.node.AbilityNode;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

public class FireballNode implements AbilityNode {
    @Override
    public Result run(AbilityContext context) {
        Player player = context.player();
        player.launchProjectile(Fireball.class);
        return Result.SUCCESS;
    }
}
