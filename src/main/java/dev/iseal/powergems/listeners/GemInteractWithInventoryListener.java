package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GemInteractWithInventoryListener implements Listener {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    private boolean hasGemInIngredients(List<ItemStack> ingredients) {
        return ingredients.stream()
                .anyMatch(gemManager::isGem);
    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void onTrade(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof MerchantInventory inventory)) {
            return;
        }

        if (hasGemInIngredients(Arrays.asList(inventory.getItem(0), inventory.getItem(1)))) {
            event.setCancelled(true);
            inventory.setItem(2, null);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onCraft(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof CraftingInventory inventory)) {
            return;
        }
        if (hasGemInIngredients(Arrays.asList(inventory.getMatrix()))) {
            inventory.setResult(null);
        }
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        //TODO: im pretty sure this causes a visual glitch with the villager
        // upgrading, but it doesnt really upgrade it so its fine .. i guess
        // -ISeal
        if (hasGemInIngredients(event.getRecipe().getIngredients())) {
            event.setCancelled(true);
        }
    }
}