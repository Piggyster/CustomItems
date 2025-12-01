package com.customitems.core.item.template;

import com.customitems.core.component.Component;
import com.customitems.core.item.ItemRarity;
import com.customitems.core.stat.ItemStatistics;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;

/**
 * Represents a template for creating items.
 * This interface defines the basic properties and methods that an item template should have.
 */

public interface Template {

    String getId(); //TODO custom id object not certain

    Material getMaterial();

    String getDisplayName();

    ItemRarity getRarity();

    ItemStatistics getStatistics();

    Map<Class<? extends Component>, Component> getComponents();

    ItemStack createItemStack();

    boolean isVanilla();
}
