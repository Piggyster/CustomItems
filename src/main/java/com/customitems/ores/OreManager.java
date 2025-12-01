package com.customitems.ores;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.Item;
import com.customitems.core.service.Service;
import com.customitems.ores.listener.BlockListener;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OreManager implements Service {

    private Map<UUID, MiningSession> sessions;

    public OreManager() {
        Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(), ItemPlugin.get());
        sessions = new HashMap<>();
        Bukkit.getScheduler().runTaskTimer(ItemPlugin.get(), () -> {
            for(Map.Entry<UUID, MiningSession> entry : sessions.entrySet()) {
                MiningSession session = entry.getValue();
                boolean complete = session.tick();
                if(complete) {
                    //removeSession(entry.getKey());
                }
            }
        }, 0, 1);
    }

    public MiningSession createSession(Player player, Block block, Item item) {
        MiningSession session = new MiningSession(player, block, item);
        sessions.put(player.getUniqueId(), session);
        return session;
    }

    public MiningSession removeSession(UUID player) {
        return sessions.remove(player);
    }

}
