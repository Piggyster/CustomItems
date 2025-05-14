package com.customitems.core.ability.node;

import com.customitems.core.ability.AbilityContext;

import java.util.ArrayList;
import java.util.List;

public class SequenceNode implements AbilityNode {

    protected final List<AbilityNode> children = new ArrayList<>();

    @Override
    public Result run(AbilityContext context) {
        for(AbilityNode node : children) {
            if(node.run(context) != Result.SUCCESS) return Result.FAILURE;
        }
        return Result.SUCCESS;
    }

    public SequenceNode add(AbilityNode node) {
        children.add(node);
        return this;
    }
}
