package com.customitems.core.listener;

import com.customitems.core.component.impl.InteractableComponent;
import com.customitems.core.handler.interactable.InteractionContext;
import com.customitems.core.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class InteractListener implements Listener {

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.getType() == Material.AIR) return;
        Item item = Item.of(itemStack);

        if(!item.hasComponent(InteractableComponent.class)) return;
        InteractableComponent component = item.getComponent(InteractableComponent.class);
        assert(component != null);

        InteractionContext context = new InteractionContext(player, item, player.getInventory().getHeldItemSlot());

        if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            component.onLeftClick(context);
        } else if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            component.onRightClick(context);
        }
    }
}
