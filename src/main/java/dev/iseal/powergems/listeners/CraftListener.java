package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.GemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import dev.iseal.powergems.managers.SingletonManager;

public class CraftListener implements Listener {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        for (ItemStack item : inventory.getMatrix()) {
            if (item != null && gemManager.isGem(item)) {
                inventory.setResult(null);
                return;
            }
        }
    }
}
