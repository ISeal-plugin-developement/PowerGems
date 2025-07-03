package dev.iseal.powergems.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;

public class TradeEventListener implements Listener {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    private boolean hasGemInIngredients(List<ItemStack> ingredients) {
        return ingredients.stream()
                .filter(Objects::nonNull)
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