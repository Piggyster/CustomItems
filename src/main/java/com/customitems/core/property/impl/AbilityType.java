package com.customitems.core.property.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public enum AbilityType {

    TELEPORT("teleport", "Teleport", "Teleport to a location.", 5, player -> {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();

        // Get the direction the player is facing
        Vector direction = playerLoc.getDirection().normalize();

        // Maximum teleport distance
        double maxDistance = 5.0;

        // Check for blocks in the path
        for (double distance = 0.5; distance <= maxDistance; distance += 0.5) {
            // Calculate position at this distance
            Location checkLoc = playerLoc.clone().add(direction.clone().multiply(distance));

            // Check if the blocks at player's body and head height are safe
            Block blockAtFeet = world.getBlockAt(checkLoc);
            Block blockAtHead = world.getBlockAt(checkLoc.clone().add(0, 1, 0));

            // If we hit a solid block, use the previous valid position
            if (blockAtFeet.getType().isSolid() || blockAtHead.getType().isSolid()) {
                // Go back half a block to avoid being inside the block
                distance -= 0.5;
                if (distance < 0.5) return; // Can't teleport at all

                // Calculate final position
                Location finalLoc = playerLoc.clone().add(direction.clone().multiply(distance));
                // Preserve original pitch and yaw
                finalLoc.setPitch(playerLoc.getPitch());
                finalLoc.setYaw(playerLoc.getYaw());

                // Teleport the player
                player.teleport(finalLoc);
                return;
            }
        }

        // No blocks found in path, teleport to maximum distance
        Location finalLoc = playerLoc.clone().add(direction.clone().multiply(maxDistance));
        // Preserve original pitch and yaw
        finalLoc.setPitch(playerLoc.getPitch());
        finalLoc.setYaw(playerLoc.getYaw());

        // Teleport the player
        player.teleport(finalLoc);
    });

    public static AbilityType fromInternalName(String internalName) {
        for (AbilityType type : values()) {
            if (type.getInternalName().equalsIgnoreCase(internalName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No AbilityType found with internal name: " + internalName);
    }

    private final String internalName;
    private final String displayName;
    private final String description;
    private final int cooldownSeconds;
    private final Consumer<Player> executor;


    AbilityType(String internalName, String displayName, String description, int cooldownSeconds, Consumer<Player> executor) {
        this.internalName = internalName;
        this.displayName = displayName;
        this.description = description;
        this.cooldownSeconds = cooldownSeconds;
        this.executor = executor;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public Consumer<Player> getExecutor() {
        return executor;
    }
}
