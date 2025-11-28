package com.customitems.core.crafting;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.ItemManager;
import com.customitems.core.item.template.Template;
import com.customitems.core.service.Services;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeManager {

    private final Set<Recipe> recipes;
    private final File recipeDirectory;
    private final Gson gson;

    public RecipeManager(File recipeDirectory) {
        recipes = new HashSet<>();
        this.recipeDirectory = recipeDirectory;
        gson = new GsonBuilder().setPrettyPrinting().create();

        if(!recipeDirectory.exists()) {
            recipeDirectory.mkdirs();
        }

        //loadRecipes();
    }

    public void registerRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public Set<Recipe> getRecipe(Template template) {
        Set<Recipe> recipes = new HashSet<>();
        for(Recipe recipe : this.recipes) {
            if(recipe.getResultTemplate().equals(template)) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    public Set<Recipe> getRecipes() {
        return ImmutableSet.copyOf(recipes);
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

    public Recipe parseRecipe(JsonObject json) {
        String recipeType = json.get("type").getAsString();
        if(recipeType.equalsIgnoreCase("shaped")) {
            return parseShapedRecipe(json);
        } else if(recipeType.equalsIgnoreCase("shapeless")) {
            return parseShapelessRecipe(json);
        }
        return null;
    }

    private Recipe parseShapedRecipe(JsonObject json) {
        JsonObject ingredientJson = json.getAsJsonObject("ingredients");

        Map<Character, RecipeIngredient> ingredients = new HashMap<>();
        for(String symbolStr : ingredientJson.keySet()) {
            char symbol = symbolStr.charAt(0);
            RecipeIngredient ingredient = parseIngredient(ingredientJson.getAsJsonObject(symbolStr));
            ingredients.put(symbol, ingredient);
        }


        String shapeStr = json.get("shape").getAsString();
        shapeStr = shapeStr.replace(" ", "");
        Map<Integer, RecipeIngredient> gridIngredients = new HashMap<>();

        int i = 0;
        for(String symbolStr : shapeStr.split("")) {
            char symbol = symbolStr.charAt(0);
            if(symbol == '0') {
                i++;
                continue;
            }

            RecipeIngredient ingredient = ingredients.get(symbol);
            if(ingredient == null) {
                throw new IllegalArgumentException("Invalid ingredient provided for symbol " + symbolStr);
            }

            gridIngredients.put(i, ingredient);
            i++;
        }

        ItemManager itemManager = Services.get(ItemManager.class);
        Template resultTemplate = itemManager.getTemplate(json.get("result").getAsString());
        if(resultTemplate == null) {
            throw new IllegalArgumentException("Invalid result template");
        }


        int baseItemSlot = -1;

        if(json.has("baseItemSlot")) {
            baseItemSlot = json.getAsJsonPrimitive("baseItemSlot").getAsInt();
        }

        return new ShapedRecipe(gridIngredients, resultTemplate, baseItemSlot); //new shaped recipe
    }

    private Recipe parseShapelessRecipe(JsonObject json) {
        JsonArray ingredientArray = json.getAsJsonArray("ingredients");
        List<RecipeIngredient> ingredients = new ArrayList<>();

        for(JsonElement ingredientJson : ingredientArray) {
            RecipeIngredient ingredient = parseIngredient(ingredientJson);
            ingredients.add(ingredient);
        }

        ItemManager itemManager = Services.get(ItemManager.class);
        Template resultTemplate = itemManager.getTemplate(json.get("result").getAsString());
        if(resultTemplate == null) {
            throw new IllegalArgumentException("Invalid result template");
        }

        return new ShapelessRecipe(ingredients, resultTemplate); //new shapeless recipe
    }

    private RecipeIngredient parseIngredient(JsonElement json) {
        ItemManager itemManager = Services.get(ItemManager.class);
        JsonObject jsonObject = json.getAsJsonObject();

        int quantity = jsonObject.has("quantity") ? jsonObject.get("quantity").getAsInt() : 1;

        String templateStr = jsonObject.get("id").getAsString();
        Template template = itemManager.getTemplate(jsonObject.get("id").getAsString());
        if(template == null) {
            throw new IllegalArgumentException("Template is null in ingredient " + templateStr);
        }

        return new RecipeIngredient(template, quantity);
    }
}
