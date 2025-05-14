package com.customitems.core.ability.node;

import com.customitems.core.ability.AbilityContext;

public interface AbilityNode {
    enum Result {
        SUCCESS,
        FAILURE,
        RUNNING;
    }
    Result run(AbilityContext context);
}
