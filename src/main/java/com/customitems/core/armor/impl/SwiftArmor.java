package com.customitems.core.armor.impl;

import com.customitems.core.ItemPlugin;
import com.customitems.core.armor.AbstractArmorSet;
import com.customitems.core.item.ItemManager;
import com.customitems.core.item.template.ItemTemplate;
import com.customitems.core.service.Services;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SwiftArmor extends AbstractArmorSet {


    public SwiftArmor() {
        super(
                Services.get(ItemManager.class).getTemplate("swift_helmet"),
                Services.get(ItemManager.class).getTemplate("swift_chestplate"),
                Services.get(ItemManager.class).getTemplate("swift_leggings"),
                Services.get(ItemManager.class).getTemplate("swift_boots")
                );
    }

    @Override
    public String getName() {
        return "Swift";
    }

    @Override
    public void onEquip(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2));
    }

    @Override
    public void onUnequip(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
    }

}
