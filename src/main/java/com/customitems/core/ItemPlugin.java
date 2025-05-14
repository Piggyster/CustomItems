package com.customitems.core;

import com.customitems.core.ability.AbilityProperty;
import com.customitems.core.ability.AbilityRegistry;
import com.customitems.core.ability.impl.TeleportAbility;
import com.customitems.core.command.GiveCommand;
import com.customitems.core.command.PropertyCommand;
import com.customitems.core.command.StatCommand;
import com.customitems.core.item.ItemManager;
import com.customitems.core.property.PropertyListener;
import com.customitems.core.property.PropertyRegistry;
import com.customitems.core.property.impl.PouchProperty;
import com.customitems.core.property.impl.StatProperty;
import com.customitems.core.service.Services;
import com.customitems.core.stat.StatListener;
import com.customitems.core.stat.StatStorage;
import com.customitems.core.property.impl.UniqueProperty;
import com.customitems.core.test.TestListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

    @Override
    public void onEnable() {
        instance = this;

        Services.register(AbilityRegistry.class, new AbilityRegistry());
        registerAbilities();
        Services.register(PropertyRegistry.class, new PropertyRegistry());
        registerProperties();
        Services.register(ItemManager.class, new ItemManager());
        Services.register(StatStorage.class, new StatStorage());


        registerEvents();

        getCommand("property").setExecutor(new PropertyCommand());
        getCommand("stat").setExecutor(new StatCommand());

        GiveCommand giveCommand = new GiveCommand();

        getCommand("give").setExecutor(giveCommand);
        getCommand("give").setTabCompleter(giveCommand);

    }

    private void registerEvents() {
        StatStorage storage = Services.get(StatStorage.class);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new StatListener(storage), this);
        //pluginManager.registerEvents(new TestListener(), this);
        pluginManager.registerEvents(new PropertyListener(), this);
    }

    private void registerProperties() {
        PropertyRegistry registry = Services.get(PropertyRegistry.class);
        registry.register(StatProperty.TYPE);
        registry.register(PouchProperty.TYPE);
        registry.register(UniqueProperty.TYPE);
        registry.register(AbilityProperty.TYPE);
    }

    private void registerAbilities() {
        AbilityRegistry registry = Services.get(AbilityRegistry.class);
        registry.register(new TeleportAbility());
    }


    public static ItemPlugin get() {
        return instance;
    }

}
