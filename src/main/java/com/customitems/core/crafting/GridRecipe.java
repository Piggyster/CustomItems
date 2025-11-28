package com.customitems.core.crafting;

import com.customitems.core.attribute.Attribute;
import com.customitems.core.item.Item;
import com.customitems.core.item.ItemManager;
import com.customitems.core.item.template.Template;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class GridRecipe implements Recipe {

    private final String id;
    private final Template resultTemplate;
    private final int resultQuantity;
    private final Map<Integer, RecipeIngredient> ingredients;
    private final int baseItemSlot;

    private GridRecipe(Builder builder) {
        id = builder.id;
        resultTemplate = builder.resultTemplate;
        resultQuantity = builder.resultQuantity;
        ingredients = Collections.unmodifiableMap(builder.ingredients);
        baseItemSlot = builder.baseItemSlot;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Template getResultTemplate() {
        return resultTemplate;
    }

    @Override
    public int getResultQuantity() {
        return resultQuantity;
    }


    public Map<Integer, RecipeIngredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(Map<Integer, Item> items) {
        for(Map.Entry<Integer, RecipeIngredient> entry : ingredients.entrySet()) {
            int slot = entry.getKey();
            RecipeIngredient ingredient = entry.getValue();

            Item item = items.get(slot);
            if(item == null) return false;

            ItemStack stack = item.getStack();
            if(!ingredient.matches(item, stack.getAmount())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Item createResult(Map<Integer, Item> items, Player player) {
        Item resultItem = new Item(resultTemplate);
        if(baseItemSlot >= 0 && items.containsKey(baseItemSlot)) {
            Item baseItem = items.get(baseItemSlot);
            for(Attribute<?> attribute : baseItem.getAttributes()) {
                resultItem.addAttribute(attribute);
            }
        }
        resultItem.getStack().setAmount(resultQuantity);
        resultItem.update(player, resultItem.getStack());

        return resultItem;
    }

    @Override
    public boolean consume(Map<Integer, Item> items) {
        return false;
    }

    public static class Builder {
        private String id;
        private Template resultTemplate;
        private int resultQuantity = 1;
        private final Map<Integer, RecipeIngredient> ingredients = new HashMap<>();
        private int baseItemSlot = -1;

        public Builder(String id) {
            this.id = id;
        }

        public Builder result(Template resultTemplate, int quantity) {
            this.resultTemplate = resultTemplate;
            resultQuantity = quantity;
            return this;
        }

        public Builder ingredient(int slot, RecipeIngredient ingredient) {
            ingredients.put(slot, ingredient);
            return this;
        }

        public Builder baseItemSlot(int slot) {
            baseItemSlot = slot;
            return this;
        }

        public GridRecipe build() {
            if (id == null || id.isEmpty()) {
                throw new IllegalStateException("Recipe ID cannot be null or empty");
            }

            if (resultTemplate == null) {
                throw new IllegalStateException("Result template ID cannot be null or empty");
            }

            if (ingredients.isEmpty()) {
                throw new IllegalStateException("Recipe must have at least one ingredient");
            }

            return new GridRecipe(this);
        }
    }
}
