package com.customitems.core.property.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public enum AbilityType {

    TELEPORT("teleport", "Teleport", "Teleport to a location.", 5, player -> {
        Vector direction = player.getLocation().getDirection().normalize();

        // Starting position is player's eyes
        Location startLoc = player.getEyeLocation().clone();
        Location targetLoc = startLoc.clone();

        // Maximum teleport distance
        double maxDistance = 7.0;
        double currentDistance = 0.0;
        double step = 0.5; // Check every half block

        // Perform ray tracing to find a valid teleport location
        while (currentDistance < maxDistance) {
            // Move forward in 0.5 block increments
            targetLoc.add(direction.clone().multiply(step));
            currentDistance += step;

            // Check if we hit a solid block
            if (!targetLoc.getBlock().isPassable()) {
                // Move back to the last valid position
                targetLoc.subtract(direction.clone().multiply(step));
                break;
            }

            // Also check block above for head room
            Location headLoc = targetLoc.clone().add(0, 1, 0);
            if (!headLoc.getBlock().isPassable()) {
                // Move back to the last valid position
                targetLoc.subtract(direction.clone().multiply(step));
                break;
            }
        }

        // Get safe ground position for teleport
        Location groundLoc = targetLoc.clone();
        while (groundLoc.getBlock().isPassable() && groundLoc.getY() > 0) {
            groundLoc.subtract(0, 1, 0);
        }
        groundLoc.add(0, 1, 0); // Move up one block to be on top of ground

        // Ensure the teleport location preserves the player's direction
        groundLoc.setPitch(player.getLocation().getPitch());
        groundLoc.setYaw(player.getLocation().getYaw());

        // Teleport player
        player.teleport(groundLoc);

        // Visual and sound effects
        player.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, player.getLocation(), 50, 0.5, 0.5, 0.5, 0.5);
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
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
