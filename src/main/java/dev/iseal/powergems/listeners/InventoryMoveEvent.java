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
     * constant used to check if a player has all PowerGems permissions.
     */
    public static final String ALL_PERMISSIONS = "powergems.*";

    /**
     * Constant used to check if a click event was triggered by pressing a number key.
     * Used in inventory click handling to detect hotbar swap actions.
     */
    public static final String NUMBER_KEY = "NUMBER_KEY";

    /**
     * I18N translation key for the message when a player attempts to
     * move gems to containers out of their inventory.
     */
    public static final String CANNOT_MOVE_GEMS = "CANNOT_MOVE_GEMS";

    /**
     * I18N translation key for the message shown when a player attempts to
     * place gems into containers other than their own inventory.
     */
    public static final String CANNOT_PLACE_GEMS_IN_CONTAINERS = "CANNOT_PLACE_GEMS_IN_CONTAINERS";
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

        if(player.getGameMode() == GameMode.CREATIVE || player.hasPermission(ALL_PERMISSIONS)) {
            return;
        }
        if (clickedItem != null && gemManager.isGem(clickedItem)) {
            boolean isMovingToOtherInventory = event.getClickedInventory() != player.getInventory();

            boolean isTransferAction = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                    event.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
                    event.getAction() == InventoryAction.PLACE_ALL ||
                    event.getAction() == InventoryAction.PLACE_ONE ||
                    event.getAction() == InventoryAction.PLACE_SOME;

            if (event.getClick().name().contains(NUMBER_KEY)) {
                ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
                if (hotbarItem == null || !gemManager.isGem(hotbarItem)) {
                    player.sendMessage(I18N.translate(CANNOT_MOVE_GEMS));
                    event.setCancelled(true);
                    return;
                }
            }

            if (isMovingToOtherInventory || isTransferAction) {
                player.sendMessage(I18N.translate(CANNOT_MOVE_GEMS));
                event.setCancelled(true);
            }
        }

        ItemStack cursorItem = event.getCursor();
        if (cursorItem != null && gemManager.isGem(cursorItem) &&
                event.getClickedInventory() != null &&
                event.getClickedInventory() != player.getInventory()) {
            player.sendMessage(I18N.translate(CANNOT_PLACE_GEMS_IN_CONTAINERS));
            event.setCancelled(true);
        }
    }
}