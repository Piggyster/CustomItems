package com.customitems.core.armor;

import com.customitems.core.armor.impl.SwiftArmor;
import com.customitems.core.item.template.ItemTemplate;
import com.customitems.core.item.template.Template;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArmorSetRegistry {

    private static final Map<Class<? extends ArmorSet>, ArmorSet> sets = new ConcurrentHashMap<>();

    static {
        register(SwiftArmor.class, new SwiftArmor());
    }

    public static void register(Class<? extends ArmorSet> clazz, ArmorSet set) {
        sets.put(clazz, set);
    }

    public static ArmorSet getSet(Class<? extends ArmorSet> clazz) {
        return sets.get(clazz);
    }

    public static ArmorSet getSet(Template template) {
        for(ArmorSet set : sets.values()) {
            if(set.getCompleteSet().contains(template)) return set;
        }
        return null;
    }

    public static ArmorSet getSet(Template helmet, Template chestplate, Template leggings, Template boots) {
        CompleteSet completeSet = new CompleteSet(helmet, chestplate, leggings, boots);
        for(ArmorSet set : sets.values()) {
            if(set.getCompleteSet().equals(completeSet)) return set;
        }
        return null;
    }

    public static ArmorSet getSet(String setName) {
        for(ArmorSet set : sets.values()) {
            if(set.getName().equalsIgnoreCase(setName)) return set;
        }
        return null;
    }
}
