package com.customitems.core.item.template;

import com.customitems.core.component.Component;
import com.customitems.core.item.ItemRarity;

import com.customitems.core.stat.ItemStatistics;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SkullTemplate extends ItemTemplate {

    private final String texture;

    public SkullTemplate(@NotNull String id, @NotNull String texture, @NotNull String displayName,
                         @NotNull ItemRarity rarity, @NotNull ItemStatistics statistics,
                         @NotNull Map<Class<? extends Component>, Component> components) {
        super(id, Material.PLAYER_HEAD, displayName, rarity, statistics, components);
        this.texture = texture;
    }

    @Override
    public ItemStack createItemStack() {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if(texture != null && !texture.isEmpty()) {
            NBT.modifyComponents(skull, nbt -> {
                ReadWriteNBT profileNbt = nbt.getOrCreateCompound("minecraft:profile");
                profileNbt.setUUID("id", UUID.randomUUID());
                ReadWriteNBT propertyNbt = profileNbt.getCompoundList("properties").addCompound();
                propertyNbt.setString("name", "textures");
                propertyNbt.setString("value", texture);
            });
        }
        return skull;
    }

    public String getTexture() {
        return texture;
    }
}
