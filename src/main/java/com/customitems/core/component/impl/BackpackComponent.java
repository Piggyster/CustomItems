package com.customitems.core.component.impl;

import com.customitems.core.attribute.Attribute;
import com.customitems.core.attribute.impl.BackpackDataAttribute;
import com.customitems.core.component.Component;
import com.customitems.core.handler.interactable.InteractionContext;
import com.customitems.core.item.Item;
import com.customitems.core.menu.BackpackMenu;
import com.google.gson.JsonElement;
import org.bukkit.entity.Player;

import java.util.Map;

public class BackpackComponent extends Component {

    private static final String KEY = "backpack";

    public static BackpackComponent deserialize(JsonElement json) {
        return new BackpackComponent(json.getAsInt());
    }

    private int size;

    public BackpackComponent(int size) {
        this.size = size;
        inherit(new InteractableComponent(this::onInteract, this::onInteract));
        inherit(new CategoryComponent("backpack"));
        inherit(new UniqueComponent());
    }

    public int getSize() {
        return size;
    }

    private void onInteract(InteractionContext context) {
        Attribute<BackpackDataAttribute.BackpackData> attribute = context.item().getAttribute(BackpackDataAttribute.class);
        if(attribute == null) {
            attribute = new BackpackDataAttribute();
        }

        BackpackDataAttribute.BackpackData data = attribute.getValue();
        if(data == null) {
            data = new BackpackDataAttribute.BackpackData(Map.of());
        }

        BackpackMenu menu = new BackpackMenu(size, data, context);
        menu.open(context.player());
    }


}
