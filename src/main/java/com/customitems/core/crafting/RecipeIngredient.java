package com.customitems.core.crafting;

import com.customitems.core.item.Item;
import com.customitems.core.item.template.Template;

public class RecipeIngredient {

    private final Template template;
    private final int quantity;

    public RecipeIngredient(Template template, int quantity) {
        this.template = template;
        this.quantity = quantity;
    }

    public Template getTemplate() {
        return template;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean matches(Item item, int count) {
        if(item == null) return false;
        if(!item.getTemplate().getId().equals(template.getId())) return false;
        return count >= quantity;
    }
}
