package com.customitems.core.item.template;

import com.customitems.core.item.ItemRarity;
import com.customitems.core.property.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

    //property supplier method
    Set<Property> getDefaultProperties();

    ItemStack createItemStack();

    boolean isVanilla();
}
