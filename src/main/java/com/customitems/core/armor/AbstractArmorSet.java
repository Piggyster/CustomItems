package com.customitems.core.armor;

import com.customitems.core.item.template.ItemTemplate;
import com.customitems.core.item.template.Template;

public abstract class AbstractArmorSet implements ArmorSet {

    private CompleteSet completeSet;

    public AbstractArmorSet(Template helmet, Template chestplate, Template leggings, Template boots) {
        completeSet = new CompleteSet(helmet, chestplate, leggings, boots);
    }

    @Override
    public int getPieceCount() {
        int count = 0;
        if(completeSet.helmet() != null) count++;
        if(completeSet.chestplate() != null) count++;
        if(completeSet.leggings() != null) count++;
        if(completeSet.boots() != null) count++;
        return count;
    }

    @Override
    public CompleteSet getCompleteSet() {
        return completeSet;
    }

    @Override
    public Template getHelmet() {
        return completeSet.helmet();
    }

    @Override
    public Template getChestplate() {
        return completeSet.chestplate();
    }

    @Override
    public Template getLeggings() {
        return completeSet.leggings();
    }

    @Override
    public Template getBoots() {
        return completeSet.boots();
    }
}
