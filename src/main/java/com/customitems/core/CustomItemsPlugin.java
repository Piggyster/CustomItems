package com.customitems.core;

import com.customitems.core.command.ExampleItems;
import com.customitems.core.command.PropertyCommand;
import com.customitems.core.inventory.InventoryManager;
import com.customitems.core.item.ItemManager;
import com.customitems.core.property.PropertyRegistry;
import com.customitems.core.property.impl.UniqueProperty;
import com.customitems.core.property.impl.ability.AbilityType;
import com.customitems.core.property.impl.ability.TeleportAbilityProperty;
import com.customitems.core.property.impl.attribute.AttributeType;
import com.customitems.core.property.impl.attribute.BasicAttributeProperty;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomItemsPlugin extends JavaPlugin {

    private static CustomItemsPlugin instance;

    private ItemManager itemManager;
    private InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        instance = this;
        itemManager = new ItemManager();
        registerProperties();
        itemManager.loadTemplatesFromJson();

        inventoryManager = new InventoryManager();
        getLogger().info("CustomItems plugin has been enabled!");

        getCommand("exampleitems").setExecutor(new ExampleItems());
        getCommand("property").setExecutor(new PropertyCommand());
    }

    private void registerProperties() {
        PropertyRegistry.registerJson("unique", json -> new UniqueProperty());
        PropertyRegistry.registerNbt("unique", nbt -> new UniqueProperty(nbt.getUUID("uuid")));
    }

    public static CustomItemsPlugin getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
