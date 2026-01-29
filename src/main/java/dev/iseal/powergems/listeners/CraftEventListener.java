package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class CraftEventListener implements Listener {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();

        final ItemStack[] matrix = inventory.getMatrix();
        if (matrix == null) return;

        if (Arrays.stream(matrix).filter(Objects::nonNull).noneMatch(gemManager::isGem)) return;

        inventory.setResult(null);
    }
}
