package com.customitems.ores;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.customitems.core.ItemPlugin;
import com.customitems.core.item.Item;
import com.customitems.core.service.Services;
import com.customitems.core.stat.StatStorage;
import com.customitems.core.stat.StatType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class MiningSession {

    private final Player player;
    private final Block block;
    //private final Item item;
    private int ticksToBreak;

    private BlockData initialData;

    private int delta = 0;
    private boolean locked;

    public MiningSession(Player player, Block block, Item item) {
        this.player = player;
        this.block = block;
        //this.item = item;
        initialData = block.getBlockData().clone();
        locked = false;

        float miningSpeed = Services.get(StatStorage.class).getStat(player.getUniqueId(), StatType.MINING_SPEED);

        int hardness = (int) (initialData.getMaterial().getHardness() * 10);

        ticksToBreak = Math.round((30 * hardness) / miningSpeed);
        if(ticksToBreak <= 0) {
            tick();
        } else if(ticksToBreak < 4) {
            ticksToBreak = 4;
        }
        Bukkit.getLogger().warning(ticksToBreak + " to break with hardness " + hardness + " and speed " + miningSpeed);
    }


    public boolean tick() {
        if(locked) {
            sendDamagePacket(0);
            return false;
        }
        if(delta >= ticksToBreak) {
            //complete
            locked = true;
            block.setBlockData(Material.BEDROCK.createBlockData());
            //block.setType(Material.BEDROCK);
            Bukkit.getScheduler().runTaskLater(ItemPlugin.get(), () -> {
                block.setBlockData(initialData);
                delta = 0;
                locked = false;
            }, 100L);
           // player.sendBlockChange(block.getLocation(), Material.BEDROCK.createBlockData());
            Bukkit.getScheduler().runTask(ItemPlugin.get(), () -> sendDamagePacket(0));
            return true;
        }

        if(block.getType() == Material.BEDROCK) return true;

        int stage = Math.min(9, (delta * 10) / ticksToBreak);

        //sendDamagePacket(stage);
        sendDamagePacket(stage);

        //player.sendBlockDamage(block.getLocation(), progress);
        delta++;
        return false;
    }

    public void end() {
        sendDamagePacket(0);
    }

    private void sendDamagePacket(int stage) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        packet.getIntegers().writeSafely(0, -player.getEntityId());
        packet.getBlockPositionModifier().writeSafely(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
        packet.getIntegers().writeSafely(1, stage);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

}
