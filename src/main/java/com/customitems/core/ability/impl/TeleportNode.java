package com.customitems.core.ability.impl;

import com.customitems.core.ability.AbilityContext;
import com.customitems.core.ability.node.AbilityNode;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class TeleportNode implements AbilityNode {



    @Override
    public Result run(AbilityContext context) {
        Player player = context.player();
        Location eye = context.player().getEyeLocation();
        Vector dir = eye.getDirection().normalize();
        World world = context.player().getWorld();

        RayTraceResult hit = world.rayTraceBlocks(
                eye, dir, 5,
                FluidCollisionMode.NEVER,   // ignore fluids
                true                        // ignore pass‑through blocks (grass, torches, etc.)
        );

        // Distance along the ray to stop (leave a small gap before the hit point)
        double eyeDist = (hit == null ? 5
                : eye.distance(hit.getHitPosition().toLocation(world)) - 0.30);

        // Convert that to feet‑level distance so we don’t bury the player’s feet
        double feetDist = Math.max(0, eyeDist - player.getEyeHeight()); // ~1.62 blocks

        // Step back until both feet and head space are clear
        Location base = player.getLocation();           // feet location
        Location target = base.add(dir.clone().multiply(feetDist));

        while (!isSpaceClear(target) && feetDist > 0) {
            feetDist -= 0.50;                            // back off half a block at a time
            target = base.clone().add(dir.clone().multiply(feetDist));
        }

        if (isSpaceClear(target)) {
            target.setYaw(base.getYaw());
            target.setPitch(base.getPitch());
            player.teleport(target);
            return Result.SUCCESS;
        } else {
            return Result.FAILURE;
        }
    }

    private static boolean isSpaceClear(Location loc) {
        Block feet = loc.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        return feet.isPassable() && head.isPassable();
    }
}
