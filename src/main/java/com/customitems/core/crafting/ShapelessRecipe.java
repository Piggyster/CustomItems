package com.customitems.core.crafting;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.Item;
import com.customitems.core.item.template.Template;
import org.bukkit.entity.Player;

import java.util.*;

public class ShapelessRecipe implements Recipe {

    private List<RecipeIngredient> ingredients;
    private Template resultTemplate;

    public ShapelessRecipe(List<RecipeIngredient> ingredients, Template resultTemplate) {
        this.ingredients = ingredients;
        this.resultTemplate = resultTemplate;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public Template getResultTemplate() {
        return resultTemplate;
    }

    @Override
    public int getResultQuantity() {
        return 1;
    }

    @Override
    public Map<Integer, RecipeIngredient> getIngredients() {
        Map<Integer, RecipeIngredient> map = new HashMap<>();
        for(int i = 0; i < ingredients.size(); i++) {
            map.put(i, ingredients.get(i));
        }
        return map;
    }

    @Override
    public boolean matches(Map<Integer, Item> items) {
        if(items.size() != ingredients.size()) return false;

        List<RecipeIngredient> remaining = new ArrayList<>(ingredients);
        //ItemPlugin.get().getLogger().warning("Searching for " + remaining.size() + " items");
        for(Item item : items.values()) {
            RecipeIngredient ingredient = remaining.stream()
                    .filter(needed -> needed.getTemplate().equals(item.getTemplate()))
                    .filter(needed -> needed.getQuantity() <= item.getStack().getAmount())
                    .findFirst().orElse(null);
            if(ingredient != null) {
                //ItemPlugin.get().getLogger().warning("Found a match for ingredient " + ingredient.getTemplate().getDisplayName() + ":" + ingredient.getQuantity() + " against " + item.getTemplate().getDisplayName() + ":" + item.getStack().getAmount());
                remaining.remove(ingredient);
            } else {
                //ItemPlugin.get().getLogger().warning("Ingredient was null against " + item.getTemplate().getDisplayName() + ":" + item.getStack().getAmount());
            }
        }
        return remaining.isEmpty();
    }

    @Override
    public Item createResult(Map<Integer, Item> items, Player player) {
        Item item = new Item(resultTemplate);
        item.getStack().setAmount(getResultQuantity());
        item.update(player, item.getStack());
        return item;
    }

    @Override
    public boolean consume(Map<Integer, Item> items) {
        if(items.size() != ingredients.size()) return false;

        List<RecipeIngredient> remaining = new ArrayList<>(ingredients);
        for(Map.Entry<Integer, Item> entry : items.entrySet()) {
            Item item = entry.getValue();
            RecipeIngredient ingredient = remaining.stream()
                    .filter(needed -> needed.getTemplate().equals(item.getTemplate()))
                    .filter(needed -> needed.getQuantity() <= item.getStack().getAmount())
                    .findFirst().orElse(null);
            if(ingredient == null) {
                return false;
            }
            item.getStack().setAmount(item.getStack().getAmount() - ingredient.getQuantity());
            if(item.getStack().getAmount() <= 0) {
                items.remove(entry.getKey());
            }
            remaining.remove(ingredient);
        }
        return remaining.isEmpty();
    }
}
