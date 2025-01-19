package dev.iseal.powergems.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import dev.iseal.powergems.managers.SingletonManager;

public class TradeListener implements Listener {

    @EventHandler
    public void onTrade(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof MerchantInventory)) {
            return;
        }
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked != null && SingletonManager.getInstance().gemManager.isGem(clicked)) {
            event.setCancelled(true);
        }
    }
}
