package com.customitems.core.component.impl;

import com.customitems.core.armor.ArmorSet;
import com.customitems.core.armor.ArmorSetRegistry;
import com.customitems.core.component.Component;
import com.customitems.core.handler.display.DisplayHandler;
import com.customitems.core.handler.display.DisplayVisitor;
import com.customitems.core.item.Item;
import com.google.gson.JsonElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArmorComponent extends Component implements DisplayHandler {

    public final static String KEY = "armor";

    private final String setName;
    private ArmorSet set;

    public static ArmorComponent deserialize(JsonElement json) {
        return new ArmorComponent(json.getAsString());
    }

    public ArmorComponent(String setName) {
        inherit(new UniqueComponent());
        this.setName = setName;
    }

    public String getSetName() {
        return setName;
    }

    @Override
    public void processDisplay(Player player, DisplayVisitor visitor) {
        if(set == null) {
            set = ArmorSetRegistry.getSet(setName);
        }

        if(set == null) return;

        int equipped = 0;
        if(player != null) {
            for(int i = 2; i <= 5; i++) {
                EquipmentSlot slot = EquipmentSlot.values()[i];
                ItemStack stack = player.getInventory().getItem(slot);
                if(stack == null || stack.getType() == Material.AIR) continue;

                Item item = Item.of(stack);
                if(item == null || !item.hasComponent(ArmorComponent.class)) continue;
                if(set.equals(ArmorSetRegistry.getSet(item.getTemplate()))) equipped++;
            }
        }

        visitor.addLore("&6Full Set Bonus &e" + equipped + "/" + set.getPieceCount());
        visitor.addLore("&7Does some shit!");
    }
}
