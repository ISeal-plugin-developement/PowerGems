package dev.iseal.powergems.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;

import java.util.Arrays;

public class CraftEventListener implements Listener {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        if (inventory.getMatrix() != null &&
            Arrays.stream(inventory.getMatrix())
                .filter(item -> item != null)
                .anyMatch(gemManager::isGem)) {
            inventory.setResult(null);
        }
    }
}
