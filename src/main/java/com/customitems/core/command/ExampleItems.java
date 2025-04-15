package com.customitems.core.command;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.inventory.CustomPlayerInventory;
import com.customitems.core.inventory.InventoryManager;
import com.customitems.core.item.CustomItem;
import com.customitems.core.item.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExampleItems implements CommandExecutor {

    private final CustomItemsPlugin plugin;

    public ExampleItems() {
        plugin = CustomItemsPlugin.getInstance();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }

        ItemManager itemManager = plugin.getItemManager();
        InventoryManager inventoryManager = plugin.getInventoryManager();
        CustomPlayerInventory inventory = inventoryManager.getPlayerInventory(player);

        CustomItem item = itemManager.createItem("exotic_sword");
        inventory.addItem(item);
        return false;
    }
}
