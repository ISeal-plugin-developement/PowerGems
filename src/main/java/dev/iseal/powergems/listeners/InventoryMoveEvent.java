package dev.iseal.powergems.listeners;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

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
    private final Logger logger = PowerGems.getPlugin().getLogger();

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

        /*
        if(player.getGameMode() == GameMode.CREATIVE || player.hasPermission("powergems.movegems")) {
            return;
        }*/

        boolean isMovingToOtherInventory = event.getClickedInventory() != player.getInventory();

        if (event.getClick() == ClickType.SWAP_OFFHAND && isMovingToOtherInventory) {
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            if (gemManager.isGem(offHandItem)) {
                player.sendMessage(I18N.translate("CANNOT_MOVE_GEMS"));
                event.setCancelled(true);
                return;
            }
        }

        if (event.getClick() == ClickType.NUMBER_KEY && isMovingToOtherInventory) {
            ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
            if (gemManager.isGem(hotbarItem)) {
                player.sendMessage(I18N.translate("CANNOT_MOVE_GEMS"));
                event.setCancelled(true);
                return;
            }
        }

        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE
            && gemManager.isGem(event.getCursor())) {
            int candidateSlot = player.getInventory().firstEmpty();
            if (candidateSlot == -1) {
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    if (!gemManager.isGem(player.getInventory().getItem(i))) {
                        candidateSlot = i;
                        break;
                    }
                }
            }
            if (candidateSlot == -1) {
                logger.severe("Couldn't find any candidate slots for gem movement in "+player.getName()+"'s inventory, this will lead to unintended behaviour.");
                logger.severe("It should only happen if their inventory is full of gems");
                return;
            }
            ItemStack candidateItem = player.getInventory().getItem(candidateSlot);
            if (candidateItem != null)
                player.getWorld().dropItemNaturally(player.getLocation(), candidateItem);
            player.getInventory().setItem(candidateSlot, event.getCursor());
            event.setCursor(null);
            event.setCancelled(true);
        }

        if (clickedItem != null && gemManager.isGem(clickedItem)) {
            boolean isTransferAction = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                    event.getAction() == InventoryAction.COLLECT_TO_CURSOR ||
                    event.getAction() == InventoryAction.PLACE_ALL ||
                    event.getAction() == InventoryAction.PLACE_ONE ||
                    event.getAction() == InventoryAction.PLACE_SOME;

            if (isMovingToOtherInventory || isTransferAction) {
                player.sendMessage(I18N.translate("CANNOT_MOVE_GEMS"));
                event.setCancelled(true);
            }
        }

        ItemStack cursorItem = event.getCursor();
        if (gemManager.isGem(cursorItem)
                && event.getClickedInventory() != null
                && event.getClickedInventory() != player.getInventory()) {
            player.sendMessage(I18N.translate("CANNOT_PLACE_GEMS_IN_CONTAINERS"));
            event.setCancelled(true);
        }
    }
}