package com.customitems.core.armor;

import com.customitems.core.item.template.Template;
import org.bukkit.entity.Player;

public interface ArmorSet {

    String getName();

    default void onEquip(Player player) {}
    default void onUnequip(Player player) {}

    CompleteSet getCompleteSet();

    int getPieceCount();

    Template getHelmet();
    Template getChestplate();
    Template getLeggings();
    Template getBoots();

    default boolean isWearing(Player player) {
        return false;
    }

}
