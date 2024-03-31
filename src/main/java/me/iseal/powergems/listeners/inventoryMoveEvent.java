package me.iseal.powergems.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class inventoryMoveEvent implements Listener {

    private final Logger l = Bukkit.getLogger();

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        Player plr = (Player) event.getWhoClicked();
        switch (event.getClick()) {
            case NUMBER_KEY:
                System.out.println("Hotbar button = "+event.getHotbarButton());
                System.out.println("Raw slot id = "+event.getRawSlot());
                AtomicInteger id = new AtomicInteger();
                List<ItemStack> inventory = Arrays.asList(plr.getInventory().getContents().clone());
                Collections.reverse(inventory);
                inventory.forEach(item -> {
                    if (item != null) {
                        System.out.println("Item id "+id+" is equal to: "+item.toString());
                    }
                    id.incrementAndGet();
                });
                break;
        }
    }

}
