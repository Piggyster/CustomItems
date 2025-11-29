package com.customitems.core.component.impl;

import com.customitems.core.ItemPlugin;
import com.customitems.core.component.Component;
import com.customitems.core.crafting.Recipe;
import com.customitems.core.crafting.RecipeManager;
import com.customitems.core.service.Services;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class CraftableComponent extends Component {

    private static final String KEY = "craftable";

    public static CraftableComponent deserialize(JsonElement json) {
        /*
        RecipeManager recipeManager = Services.get(RecipeManager.class);

        JsonArray recipeArray = json.getAsJsonObject().getAsJsonArray("recipes");

        Set<Recipe> recipes = new HashSet<>();
        for(int i = 0; i < recipeArray.size(); i++) {
            JsonObject recipeJson = recipeArray.get(i).getAsJsonObject();

            Recipe recipe = recipeManager.parseRecipe(recipeJson);
            if(recipe == null) continue;
            recipeManager.registerRecipe(recipe);
            recipes.add(recipe);
        }
        return new RecipeComponent(recipes);
        */
        return new CraftableComponent(json.getAsJsonObject());
    }

    private Set<Recipe> recipes;
    private JsonObject json;

    public CraftableComponent(JsonObject json) {
        this.json = json;
    }

    public CraftableComponent(Set<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void postInit() {
        RecipeManager recipeManager = Services.get(RecipeManager.class);

        JsonArray recipeArray = json.getAsJsonObject().getAsJsonArray("recipes");

        recipes = new HashSet<>();
        for(int i = 0; i < recipeArray.size(); i++) {
            JsonObject recipeJson = recipeArray.get(i).getAsJsonObject();

            Recipe recipe = recipeManager.parseRecipe(recipeJson);
            if(recipe == null) {
                ItemPlugin.get().getLogger().warning("Recipe is null");
                continue;
            }
            recipeManager.registerRecipe(recipe);
            recipes.add(recipe);
        }
        json = null;
    }
}
