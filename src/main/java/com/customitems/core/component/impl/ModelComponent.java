package com.customitems.core.component.impl;

import com.customitems.core.component.Component;
import com.customitems.core.item.Item;
import com.google.gson.JsonElement;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ModelComponent extends Component {

    private static final String KEY = "model";

    public static ModelComponent deserialize(JsonElement json, String templateId) {
        return new ModelComponent(templateId);
    }

    private NamespacedKey namespacedKey;

    public ModelComponent(String modelStr) {
        namespacedKey = new NamespacedKey("customitem", modelStr);
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public void updateItem(Item item, ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.setItemModel(namespacedKey);
        stack.setItemMeta(meta);
    }
}
