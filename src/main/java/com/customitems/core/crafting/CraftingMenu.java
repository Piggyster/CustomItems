package com.customitems.core.crafting;

import com.customitems.core.ItemPlugin;
import com.customitems.core.item.Item;
import com.customitems.core.service.Services;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class CraftingMenu implements Listener {

    private static final ItemStack BORDER_ITEM;
    private static final ItemStack PLACEHOLDER_ITEM;

    static {
        BORDER_ITEM = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = BORDER_ITEM.getItemMeta();
        borderMeta.setDisplayName(" ");
        BORDER_ITEM.setItemMeta(borderMeta);

        PLACEHOLDER_ITEM = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta resultMeta = PLACEHOLDER_ITEM.getItemMeta();
        resultMeta.setDisplayName(ChatColor.RED + "No Recipe Matched!");
        PLACEHOLDER_ITEM.setItemMeta(resultMeta);
    }

    private static final int GRID_SIZE = 3;
    private static final int GRID_START_SLOT = 10;
    private static final int RESULT_SLOT = 24;

    private final Player player;
    private final Inventory inventory;
    private final RecipeManager recipeManager;

    private Recipe matchedRecipe = null;

    public CraftingMenu(Player player) {
        this.player = player;
        recipeManager = Services.get(RecipeManager.class);

        inventory = Bukkit.createInventory(player, 45, "Crafting Menu");
        initializeLayout();
        Bukkit.getPluginManager().registerEvents(this, ItemPlugin.get());
    }

    public void open() {
        player.openInventory(inventory);
    }

    private void initializeLayout() {

        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BORDER_ITEM.clone());
        }

        for(int row = 0; row < GRID_SIZE; row++) {
            for(int col = 0; col < GRID_SIZE; col++) {
                int slot = GRID_START_SLOT + (row * 9) + col;
                inventory.setItem(slot, null);
            }
        }

        inventory.setItem(RESULT_SLOT, PLACEHOLDER_ITEM.clone());
    }

    private boolean isGridSlot(int slot) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int gridSlot = GRID_START_SLOT + (row * 9) + col;
                if (slot == gridSlot) {
                    return true;
                }
            }
        }
        return false;
    }

    private int slotToGridPosition(int slot) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int gridSlot = GRID_START_SLOT + (row * 9) + col;
                if (slot == gridSlot) {
                    return row * GRID_SIZE + col;
                }
            }
        }
        return -1;
    }

    private void updateResult() {
        // Get items from the crafting grid
        Map<Integer, Item> gridItems = new HashMap<>();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int slot = GRID_START_SLOT + (row * 9) + col;
                int gridPos = row * GRID_SIZE + col;

                ItemStack stack = inventory.getItem(slot);
                if (stack != null && stack.getType() != Material.AIR) {
                    Item item = Item.of(stack);
                    gridItems.put(gridPos, item);
                }
            }
        }

        // Find a matching recipe
        matchedRecipe = findMatchingRecipe(gridItems);

        // Clear current result
        inventory.setItem(RESULT_SLOT, null);

        // If a recipe matches, set the result
        if (matchedRecipe != null) {
            Item resultItem = matchedRecipe.createResult(gridItems, player);
            if (resultItem != null) {
                inventory.setItem(RESULT_SLOT, resultItem.getStack());
            }
        } else {
            inventory.setItem(RESULT_SLOT, PLACEHOLDER_ITEM);
        }
    }

    private Recipe findMatchingRecipe(Map<Integer, Item> gridItems) {
        for (Recipe recipe : recipeManager.getRecipes()) {
            if (recipe.matches(gridItems)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean consumeIngredients() {
        if (matchedRecipe == null) return false;

        // Get items from the crafting grid
        Map<Integer, Item> gridItems = new HashMap<>();
        Map<Integer, ItemStack> gridItemStacks = new HashMap<>();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int slot = GRID_START_SLOT + (row * 9) + col;
                int gridPos = row * GRID_SIZE + col;

                ItemStack stack = inventory.getItem(slot);
                if (stack != null && stack.getType() != Material.AIR) {
                    gridItemStacks.put(gridPos, stack.clone());
                    Item item = Item.of(stack);
                    gridItems.put(gridPos, item);
                }
            }
        }

        if(!matchedRecipe.matches(gridItems)) {
            return false;
        }

        // Consume ingredients
        for (Map.Entry<Integer, RecipeIngredient> entry : matchedRecipe.getIngredients().entrySet()) {
            int gridPos = entry.getKey();
            RecipeIngredient ingredient = entry.getValue();

            ItemStack stack = gridItemStacks.get(gridPos);
            if (stack != null) {
                int newAmount = stack.getAmount() - ingredient.getQuantity();

                if (newAmount <= 0) {
                    // Remove the item
                    int slot = getSlotFromGridPos(gridPos);
                    inventory.setItem(slot, null);
                } else {
                    // Reduce the amount
                    stack.setAmount(newAmount);
                    int slot = getSlotFromGridPos(gridPos);
                    inventory.setItem(slot, stack);
                }
            }
        }

        // Clear the result slot (the item was taken by the player)
        //inventory.setItem(RESULT_SLOT, null);

        // Update the result for the next potential craft
        //updateResult();
        return true;
    }

    private int getSlotFromGridPos(int gridPos) {
        int row = gridPos / GRID_SIZE;
        int col = gridPos % GRID_SIZE;
        return GRID_START_SLOT + (row * 9) + col;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        int slot = event.getRawSlot();

        // Allow clicking in player inventory
        if (slot >= inventory.getSize()) return;

        // Handle result slot
        if (slot == RESULT_SLOT) {
            if (matchedRecipe != null) {
                // Don't allow placing items in the result slot
                if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                    event.setCancelled(true);
                    return;
                }

                // Only allow taking items if there's a result
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR &&
                        !event.getCurrentItem().isSimilar(PLACEHOLDER_ITEM) &&
                        !event.getCurrentItem().isSimilar(BORDER_ITEM)) {
                    ItemStack resultCopy = event.getCurrentItem().clone();

                    boolean success = consumeIngredients();
                    if(success) {
                        if(event.getCursor() != null || !event.getCursor().getType().isAir()) {
                            event.setCancelled(true);
                            return;
                        } //additional check
                        event.getView().setCursor(resultCopy);
                        updateResult();
                        return;
                    } else {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    event.setCancelled(true);
                }
                return;
            } else {
                event.setCancelled(true);
                return;
            }
        }

        // Allow modifying the crafting grid
        if (isGridSlot(slot)) {
            // Let the event proceed normally
            // The result will be updated after the click
            Bukkit.getScheduler().runTask(ItemPlugin.get(), this::updateResult);
            return;
        }

        // Cancel clicks on border/background items
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        // Check if any of the drag slots are in the restricted area
        for (int slot : event.getRawSlots()) {
            if (slot < inventory.getSize() && !isGridSlot(slot)) {
                event.setCancelled(true);
                return;
            }
        }

        // Update the result after the drag
        Bukkit.getScheduler().runTask(ItemPlugin.get(), this::updateResult);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        // Return any items in the crafting grid to the player
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int slot = GRID_START_SLOT + (row * 9) + col;
                ItemStack item = inventory.getItem(slot);

                if (item != null && item.getType() != Material.AIR) {
                    HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(item);

                    // Drop any items that couldn't fit in the inventory
                    for (ItemStack overflowItem : overflow.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), overflowItem);
                    }
                }
            }
        }

        // Unregister listener
        HandlerList.unregisterAll(this);
    }
}
