package com.customitems.core.armor;

import com.customitems.core.item.template.Template;

public record CompleteSet(Template helmet, Template chestplate, Template leggings, Template boots) {

    public boolean contains(Template template) {
        if(helmet.equals(template)) return true;
        if(chestplate.equals(template)) return true;
        if(leggings.equals(template)) return true;
        if(boots.equals(template)) return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof CompleteSet completeSet)) return false;
        if(helmet != null && !helmet.equals(completeSet.helmet)) return false;
        if(chestplate != null && !chestplate.equals(completeSet.chestplate)) return false;
        if(leggings != null && !leggings.equals(completeSet.leggings)) return false;
        if(boots != null && !boots.equals(completeSet.boots)) return false;
        return true;
    }
}
