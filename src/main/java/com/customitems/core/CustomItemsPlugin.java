package com.customitems.core;

import com.customitems.core.command.ExampleItems;
import com.customitems.core.command.PropertyCommand;
import com.customitems.core.inventory.InventoryManager;
import com.customitems.core.item.ItemManager;
import com.customitems.core.property.PropertyRegistry;
import com.customitems.core.property.impl.UniqueProperty;
import com.customitems.core.stat.StatType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class CustomItemsPlugin extends JavaPlugin {

    private static CustomItemsPlugin instance;

    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;
        itemManager = new ItemManager();
        registerProperties();
        itemManager.loadTemplatesFromJson();

        getLogger().info("CustomItems plugin has been enabled!");

        getCommand("exampleitems").setExecutor(new ExampleItems());
        getCommand("property").setExecutor(new PropertyCommand());
    }

    private void registerProperties() {
        PropertyRegistry.registerJson("unique", json -> new UniqueProperty());
        PropertyRegistry.registerNbt("unique", nbt -> new UniqueProperty(nbt.getUUID("uuid")));

        PropertyRegistry.registerJson("stats", json -> {
            Map<StatType, Integer> stats = new HashMap<>();
            json.getAsJsonObject("stats");
        });
    }

    public static CustomItemsPlugin getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

}
