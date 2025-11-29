package com.customitems.core.component;

import com.customitems.core.item.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    private final List<Component> inherited = new ArrayList<>();

    public Component() {

    }

    public void updateItem(Item item, ItemStack stack) {

    }

    protected void inherit(Component component) {
        inherited.add(component);
    }

    public List<Component> getInherited() {
        return inherited;
    }
}
