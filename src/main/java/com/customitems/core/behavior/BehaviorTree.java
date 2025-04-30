package com.customitems.core.behavior;

import java.util.List;

public class BehaviorTree {

    private final List<BehaviorNode> nodes;

    public BehaviorTree(List<BehaviorNode> nodes) {
        this.nodes = nodes;
    }

    public void execute(CustomItemContext context) {
        for(BehaviorNode node : nodes) {
            if (!node.execute(context)) {
                break;
            }
        }
    }
}
