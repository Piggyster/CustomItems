package com.customitems.v2.item;

import com.customitems.v2.property.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

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
