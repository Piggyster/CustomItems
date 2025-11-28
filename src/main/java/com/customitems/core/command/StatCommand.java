package com.customitems.core.command;

import com.customitems.core.crafting.Recipe;
import com.customitems.core.crafting.RecipeManager;
import com.customitems.core.crafting.ShapelessRecipe;
import com.customitems.core.service.Services;
import com.customitems.core.stat.StatStorage;
import com.customitems.core.stat.StatType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StatCommand implements CommandExecutor {

    private final StatStorage storage;

    public StatCommand() {
        storage = Services.get(StatStorage.class);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return false;

        RecipeManager recipeManager = Services.get(RecipeManager.class);

        Recipe recipe = recipeManager.getRecipes().stream().filter(r -> r instanceof ShapelessRecipe).findAny().get();
        recipe.getIngredients().forEach((gridPos, ingredient) -> {
            player.sendMessage(gridPos + ": " + ingredient.getTemplate().getDisplayName());
        });


        return false;
    }
}