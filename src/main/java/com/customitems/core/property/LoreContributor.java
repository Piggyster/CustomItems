package com.customitems.core.property;

/**
 * Interface for classes that can contribute lore to an item.
 * This is used to add custom lore to items in the game.
 */

public interface LoreContributor {

    int getLorePriority();

    void contributeLore(LoreVisitor visitor);

}
