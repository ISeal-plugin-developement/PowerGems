package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.GemManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BundleListener implements Listener {

    private final GemManager gm = GemManager.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory or the item is null
        if (event.getCurrentItem() == null && event.getCursor() == null) return;

        // Check if the slot is a bundle
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        // Prevent placing a gem into a bundle
        if (clicked != null && clicked.getType().toString().contains("BUNDLE") && gm.isGem(cursor)) {
            event.setCancelled(true);
        }
        // Prevent swapping a gem from a bundle to inventory
        if (cursor != null && cursor.getType().toString().contains("BUNDLE") && gm.isGem(clicked)) {
            event.setCancelled(true);
        }
    }
}
