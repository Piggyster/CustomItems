package com.customitems.core.command;

import com.customitems.core.CustomItemsPlugin;
import com.customitems.core.item.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PropertyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return false;

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if(heldItem.getType().isAir()) return false;

        CustomItem customItem = CustomItemsPlugin.getInstance().getItemManager().getCustomItem(heldItem).get();

        customItem.getProperties().forEach(property -> {
            player.sendMessage(property.toString());
        });




        return false;
    }
}
