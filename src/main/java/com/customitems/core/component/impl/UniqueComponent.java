package com.customitems.core.component.impl;

import com.customitems.core.attribute.impl.UniqueAttribute;
import com.customitems.core.component.Component;
import com.customitems.core.handler.LoreHandler;
import com.customitems.core.item.Item;
import com.google.gson.JsonElement;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class UniqueComponent extends Component implements LoreHandler {

    public static final String KEY = "unique";

    public static UniqueComponent deserialize(JsonElement json) {
        return new UniqueComponent();
    }

    @Override
    public void updateItem(Item item) {
        if(item.hasAttribute(UniqueAttribute.class)) return;

        UniqueAttribute attribute = new UniqueAttribute();
        attribute.setValue(UUID.randomUUID());
        item.addAttribute(attribute);
    }

    @Override
    public List<String> contributeLore(Item item, Player player) {
        return List.of("&8This item is special.");
    }
}
