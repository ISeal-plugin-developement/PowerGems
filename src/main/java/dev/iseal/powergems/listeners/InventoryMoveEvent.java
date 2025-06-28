package dev.iseal.powergems.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;

/**
 * InventoryMoveEvent is a listener that prevents players from moving gems
 * into other inventories or containers.
 * Crafting tables are exempt to allow gem crafting.
 */
public class InventoryMoveEvent implements Listener {

    /**
     * The GemManager instance
     */
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    /**
     * Prevents the players from moving gems into other inventories,
     * crafting tables are exempted from this check to allow crafting gems.
     *
     * @param event the InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryMove(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if(event.getInventory().getType() == InventoryType.WORKBENCH) {
            return; //Exempt the crafting table so players can craft gems
        }

        ItemStack clickedItem = event.getCurrentItem();

        if(player.getGameMode() == GameMode.CREATIVE || player.hasPermission("powergems.movegems")) {
            return;
        }
        if (clickedItem != null && gemManager.isGem(clickedItem)) {
            boolean isMovingToOtherInventory = event.getClickedInventory() != player.getInventory();

            boolean isTransferAction = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                    event.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
                    event.getAction() == InventoryAction.PLACE_ALL ||
                    event.getAction() == InventoryAction.PLACE_ONE ||
                    event.getAction() == InventoryAction.PLACE_SOME;

            if (event.getClick().name().contains("NUMBER_KEY")) {
                ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
                if (hotbarItem == null || !gemManager.isGem(hotbarItem)) {
                    player.sendMessage(I18N.translate("CANNOT_MOVE_GEMS"));
                    event.setCancelled(true);
                    return;
                }
            }

            if (isMovingToOtherInventory || isTransferAction) {
                player.sendMessage(I18N.translate("CANNOT_MOVE_GEMS"));
                event.setCancelled(true);
            }
        }

        ItemStack cursorItem = event.getCursor();
        if (cursorItem != null && gemManager.isGem(cursorItem) &&
                event.getClickedInventory() != null &&
                event.getClickedInventory() != player.getInventory()) {
            player.sendMessage(I18N.translate("CANNOT_PLACE_GEMS_IN_CONTAINERS"));
            event.setCancelled(true);
        }
    }
}