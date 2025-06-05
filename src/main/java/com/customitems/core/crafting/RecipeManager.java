package com.customitems.core.crafting;

import com.customitems.core.item.ItemManager;
import com.customitems.core.item.template.Template;
import com.customitems.core.service.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeManager {

    private final Map<String, Recipe> recipes;
    private final File recipeDirectory;
    private final Gson gson;

    public RecipeManager(File recipeDirectory) {
        recipes = new ConcurrentHashMap<>();
        this.recipeDirectory = recipeDirectory;
        gson = new GsonBuilder().setPrettyPrinting().create();

        if(!recipeDirectory.exists()) {
            recipeDirectory.mkdirs();
        }

        loadRecipes();
    }

    public void registerRecipe(Recipe recipe) {
        recipes.put(recipe.getId(), recipe);
    }

    public Recipe getRecipe(String id) {
        return recipes.get(id);
    }

    public Collection<Recipe> getRecipes() {
        return Collections.unmodifiableCollection(recipes.values());
    }

    private void loadRecipes() {
        if(!recipeDirectory.exists() || !recipeDirectory.isDirectory()) return;

        File[] files = recipeDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if(files == null) return;

        for(File file : files) {
            try(FileReader reader = new FileReader(file)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                Recipe recipe = parseRecipe(json);
                if(recipe != null) {
                    registerRecipe(recipe);
                    Bukkit.getLogger().info("Loaded recipe: " + recipe.getId() + " from " + file.getName());
                }
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Recipe parseRecipe(JsonObject json) {
        ItemManager itemManager = Services.get(ItemManager.class);
        try {
            String id = json.get("id").getAsString();
            GridRecipe.Builder builder = new GridRecipe.Builder(id);
            Template resultTemplate = itemManager.getTemplate(json.get("result").getAsString());
            if(resultTemplate == null) {
                Bukkit.getLogger().warning("Failed to load recipe: " + id + " - Result template not found");
                return null;
            }
            builder.result(
                    resultTemplate,
                    json.has("resultQuantity") ? json.get("resultQuantity").getAsInt() : 1
            );

            JsonObject ingredientsJson = json.getAsJsonObject("ingredients");
            for(String slotStr : ingredientsJson.keySet()) {
                int slot = Integer.parseInt(slotStr);
                JsonObject ingredientJson = ingredientsJson.getAsJsonObject(slotStr);

                Template template = itemManager.getTemplate(ingredientJson.get("id").getAsString());
                int quantity = json.has("quantity") ? ingredientJson.get("quantity").getAsInt() : 1;

                builder.ingredient(slot, new RecipeIngredient(template, quantity));
            }

            if(json.has("baseItemSlot")) {
                builder.baseItemSlot(json.get("baseItemSlot").getAsInt());
            }

            return builder.build();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
