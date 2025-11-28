package com.customitems.core.crafting;

import com.customitems.core.ItemPlugin;
import com.customitems.core.attribute.Attribute;
import com.customitems.core.item.Item;
import com.customitems.core.item.template.Template;
import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShapedRecipe implements Recipe {

    private Map<Integer, RecipeIngredient> ingredients;
    private Template resultTemplate;
    private int baseItemSlot;

    public ShapedRecipe(Map<Integer, RecipeIngredient> ingredients, Template resultTemplate, int baseItemSlot) {
        this.ingredients = ingredients;
        this.resultTemplate = resultTemplate;
        this.baseItemSlot = baseItemSlot;
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
        return ImmutableMap.copyOf(ingredients);
    }

    @Override
    public boolean matches(Map<Integer, Item> items) {
        /*for(Map.Entry<Integer, RecipeIngredient> entry : ingredients.entrySet()) {
            int slot = entry.getKey();
            RecipeIngredient ingredient = entry.getValue();

            Item item = items.get(slot);
            if(item == null) return false;

            ItemStack stack = item.getStack();
            if(!ingredient.matches(item, stack.getAmount())) {
                return false;
            }
        }
        return true;*/

        int[] bounds = getBounds();
        int recipeWidth = bounds[2] - bounds[0] + 1;
        int recipeHeight = bounds[3] - bounds[1] + 1;
        for(int offsetX = 0; offsetX <= 3 - recipeWidth; offsetX++) {
            for(int offsetY = 0; offsetY <= 3 - recipeHeight; offsetY++) {
                if(matchesWithOffset(items, offsetX, offsetY, bounds)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesWithOffset(Map<Integer, Item> items, int offsetX, int offsetY, int[] bounds) {
        for(Map.Entry<Integer, RecipeIngredient> entry : ingredients.entrySet()) {
            int ingredientX = entry.getKey() % 3;
            int ingredientY = entry.getKey() / 3;

            int normalizedX = ingredientX - bounds[0];
            int normalizedY = ingredientY - bounds[1];

            // Apply offset
            int gridX = normalizedX + offsetX;
            int gridY = normalizedY + offsetY;

            // Convert back to grid slot
            int gridPos = gridY * 3 + gridX;

            Item item = items.get(gridPos);
            if(item == null) {
                return false;
            }
            if(!entry.getValue().matches(item, item.getStack().getAmount())) {
                return false;
            }
        }

        for (int i = 0; i < 9; i++) {
            int gridX = i % 3;
            int gridY = i / 3;

            // Check if this grid position corresponds to a recipe ingredient
            boolean isRecipeSlot = false;

            for(int recipeSlot : ingredients.keySet()) {
                int recipeX = recipeSlot % 3;
                int recipeY = recipeSlot / 3;
                int normalizedX = recipeX - bounds[0];
                int normalizedY = recipeY - bounds[1];

                if(gridX == normalizedX + offsetX && gridY == normalizedY + offsetY) {
                    isRecipeSlot = true;
                    break;
                }
            }

            if(!isRecipeSlot) {
                Item item = items.get(i);
                if(item != null) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[] getBounds() {
        int minX = 3, minY = 3, maxX = -1, maxY = -1;

        for(int pos : ingredients.keySet()) {
            int x = pos % 3;
            int y = pos / 3;

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        return new int[]{minX, minY, maxX, maxY};
    }

    @Override
    public Item createResult(Map<Integer, Item> items, Player player) {
        Item resultItem = new Item(resultTemplate);
        if(baseItemSlot >= 0) {
            int[] bounds = getBounds();
            int recipeWidth = bounds[2] - bounds[0] + 1;
            int recipeHeight = bounds[3] - bounds[1] + 1;
            int offsetX = 0, offsetY = 0;
            loop: for(offsetX = 0; offsetX <= 3 - recipeWidth; offsetX++) {
                for(offsetY = 0; offsetY <= 3 - recipeHeight; offsetY++) {
                    if(matchesWithOffset(items, offsetX, offsetY, bounds)) {
                        break loop;
                    }
                }
            }

            int ingredientX = baseItemSlot % 3;
            int ingredientY = baseItemSlot / 3;

            int normalizedX = ingredientX - bounds[0];
            int normalizedY = ingredientY - bounds[1];

            // Apply offset
            int gridX = normalizedX + offsetX;
            int gridY = normalizedY + offsetY;

            // Convert back to grid slot
            int gridPos = gridY * 3 + gridX;
            ItemPlugin.get().getLogger().warning("Base Item Slot: " + gridPos);

            Item baseItem = items.get(gridPos);
            for(Attribute<?> attribute : baseItem.getAttributes()) {
                resultItem.addAttribute(attribute);
            }
        }
        resultItem.getStack().setAmount(getResultQuantity());
        resultItem.update(player, resultItem.getStack());

        return resultItem;
    }

    @Override
    public boolean consume(Map<Integer, Item> items) { //ItemStack ??
        /*if(!matches(items)) {
            return false;
        }*/

        int[] bounds = getBounds();
        int recipeWidth = bounds[2] - bounds[0] + 1;
        int recipeHeight = bounds[3] - bounds[1] + 1;

        for(int offsetX = 0; offsetX <= 3 - recipeWidth; offsetX++) {
            for(int offsetY = 0; offsetY <= 3 - recipeHeight; offsetY++) {
                if (matchesWithOffset(items, offsetX, offsetY, bounds)) {
                    // Consume at this offset
                    consumeAtOffset(items, offsetX, offsetY, bounds);
                    return true;
                }
            }
        }
        return false;

        /*
        for (Map.Entry<Integer, RecipeIngredient> entry : ingredients.entrySet()) {
            int gridPos = entry.getKey();
            RecipeIngredient ingredient = entry.getValue();

            Item item = items.get(gridPos);
            if (item != null) {
                int newAmount = item.getStack().getAmount() - ingredient.getQuantity();

                if (newAmount <= 0) {
                    items.remove(gridPos);
                    // Remove the item
                } else {
                    item.getStack().setAmount(newAmount);
                    // Reduce the amount
                }
            }
        }
        return true;*/
    }

    private void consumeAtOffset(Map<Integer, Item> items, int offsetX, int offsetY, int[] bounds) {
        for(Map.Entry<Integer, RecipeIngredient> entry : ingredients.entrySet()) {
            int recipeSlot = entry.getKey();
            int requireQuantity = entry.getValue().getQuantity();

            int recipeX = recipeSlot % 3;
            int recipeY = recipeSlot / 3;
            int normalizedX = recipeX - bounds[0];
            int normalizedY = recipeY - bounds[1];
            int gridX = normalizedX + offsetX;
            int gridY = normalizedY + offsetY;
            int gridPos = gridY * 3 + gridX;

            Item item = items.get(gridPos);
            if(item != null) {
                item.getStack().setAmount(item.getStack().getAmount() - requireQuantity);
            }
        }
    }
}
