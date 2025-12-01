package com.customitems.core.component.impl;

import com.customitems.core.component.Component;
import com.google.gson.JsonElement;

public class PickaxeComponent extends Component {

    private static final String KEY = "pickaxe";

    public static PickaxeComponent deserialize(JsonElement json) {
        return new PickaxeComponent(json.getAsInt());
    }

    private int speed;

    public PickaxeComponent(int speed) {
        this.speed = speed;
        inherit(new UniqueComponent());
        inherit(new CategoryComponent("pickaxe"));
    }

    public int getSpeed() {
        return speed;
    }
}
