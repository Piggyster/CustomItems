package com.customitems.core.command;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.Item;
import com.customitems.core.item.ItemManager;
import com.customitems.core.item.template.Template;
import com.customitems.core.service.Services;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GiveCommand implements CommandExecutor, TabCompleter {

    private final ItemManager itemManager;

    public GiveCommand() {
        itemManager = Services.get(ItemManager.class);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 2) {
            sender.sendMessage("Usage: /give <player> <template> [quantity]");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null || !target.isOnline()) {
            sender.sendMessage(args[0] + " is not online.");
            return false;
        }

        Template template = itemManager.getTemplate(args[1]);
        if(template == null) {
            sender.sendMessage(args[1] + " is not a valid template.");
            return false;
        }

        int quantity = 1;
        if(args.length == 3) {
            try {
                quantity = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(args[2] + " is not a valid number.");
                return false;
            }
        }

        ItemStack stack = template.createItemStack();
        stack.setAmount(quantity);

        Item item = new Item(stack, template);
        ItemStack itemStack = item.update(target.getPlayer(), item.getStack());
        target.getInventory().addItem(itemStack);
        sender.sendMessage("Gave " + quantity + " " + template.getId() + " to " + target.getName() + ".");
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        } else if(args.length == 2) {
            return itemManager.getTemplates().stream().map(Template::getId).toList();
        }
        return List.of();
    }
}
