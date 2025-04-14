package com.customitems.core;

import com.customitems.core.command.ExampleItems;
import com.customitems.core.command.PropertyCommand;
import com.customitems.core.inventory.InventoryManager;
import com.customitems.core.item.ItemManager;
import com.customitems.core.property.PropertyRegistry;
import com.customitems.core.property.impl.*;
import org.bukkit.NamespacedKey;
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
        PropertyRegistry.registerJsonFactory("attribute", jsonObject -> {
            String attributeTypeName = jsonObject.get("attributeType").getAsString();
            AttributeType attributeType = AttributeType.fromInternalName(attributeTypeName.toLowerCase());
            double baseValue = jsonObject.get("value").getAsDouble();
            return new AttributeProperty(attributeType, baseValue);
        });

        PropertyRegistry.registerJsonFactory("sharp", jsonObject -> new SharpProperty());
        PropertyRegistry.registerPropertyFactory("sharp", nbt -> new SharpProperty());

        PropertyRegistry.registerJsonFactory("ability", jsonObject -> {
            String abilityTypeName = jsonObject.get("abilityType").getAsString();
            AbilityType abilityType = AbilityType.fromInternalName(abilityTypeName.toLowerCase());
            return new AbilityProperty(abilityType);
        });
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
