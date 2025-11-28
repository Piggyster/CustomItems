package com.customitems.core.crafting;

import com.customitems.core.item.Item;
import com.customitems.core.item.template.Template;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Recipe {

    String getId();

    Template getResultTemplate();

    int getResultQuantity();

    Map<Integer, RecipeIngredient> getIngredients();

    boolean matches(Map<Integer, Item> items);

    Item createResult(Map<Integer, Item> items, Player player);

    boolean consume(Map<Integer, Item> items);

}
