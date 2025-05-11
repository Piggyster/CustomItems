package com.customitems.core;

import com.customitems.core.item.ItemManager;
import com.customitems.core.property.PropertyRegistry;
import com.customitems.core.property.impl.SharpProperty;
import com.customitems.core.property.impl.StatProperty;
import com.customitems.core.property.impl.StatType;
import com.customitems.core.property.impl.UniqueProperty;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * CustomItems Plugin
 * A sophisticated wrapper system for all ItemStacks
 * to provide a expandable plug and play system
 * using Property objects and Template objects
 *
 * @author Piggyster
 */

public class ItemPlugin extends JavaPlugin {

    private static ItemPlugin instance;
    private PropertyRegistry registry;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;
        registry = new PropertyRegistry();
        itemManager = new ItemManager();

        registry.register(
                StatProperty.class,
                "stats",
                json -> {
                    Map<StatType, Integer> stats = new HashMap<>();
                    for(String statType : json.keySet()) {
                        StatType type = StatType.valueOf(statType.toUpperCase());
                        int value = json.getAsJsonObject(statType).getAsInt();
                        stats.put(type, value);
                    }
                    return new StatProperty(stats);
                }, null);

        //TODO solution for loose properties
        //TODO maybe a LooseProperty extension
        //these are properties that are defined to exist, maybe hold their own nbt value
        //but they're loaded from templates with json, and then init with the nbt
        //these are properties who are both json AND nbt
        //properties that aren't like this are regular, solid properties that handle their
        //loading at registry time, instead of at init time
        registry.register(
                SharpProperty.class,
                "sharp",
                json -> new SharpProperty(),
                nbt -> new SharpProperty()
        );

        registry.register(
                UniqueProperty.class,
                "uuid",
                json -> new UniqueProperty(),
                nbt -> new UniqueProperty()
        );
    }


    public static ItemPlugin get() {
        return instance;
    }

    public PropertyRegistry getRegistry() {
        return registry;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
