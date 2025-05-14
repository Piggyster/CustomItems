package com.customitems.core.command;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.ItemManager;
import com.customitems.core.service.Services;
import com.customitems.core.stat.StatStorage;
import com.customitems.core.stat.StatType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StatCommand implements CommandExecutor {

    private final StatStorage storage;

    public StatCommand() {
        storage = Services.get(StatStorage.class);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return false;

        Map<StatType, Double> stats = storage.getStats(player.getUniqueId());

        player.sendMessage("Your stats:");
        stats.forEach((stat, value) -> {
            player.sendMessage(stat.toString() + ": " + value);
        });

        return false;
    }
}
