package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryCloseListener implements Listener {

    private ItemStack randomGem = null;
    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final GeneralConfigManager gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (randomGem == null) {
            randomGem = gm.getRandomGemItem();
        }
        if (!(e.getInventory().getHolder() instanceof Player plr)) {
            return;
        }
        if (!(e.getView().getBottomInventory() instanceof PlayerInventory pi)) {
            return;
        }
        //checkIfMultipleGems(plr);
        if (!e.getView().getBottomInventory().containsAtLeast(randomGem, 1)) {
            return;
        }
        int nOfGems = 0;
        int intAt = -1;
        for (ItemStack item : pi.getContents()) {
            intAt++;
            if (item == null || !item.isSimilar(randomGem)) {
                continue;
            }
            nOfGems += item.getAmount();
            pi.setItem(intAt, null);
        }
        if (gcm.allowOnlyOneGem()) {
            if (pi.firstEmpty() == -1)
                plr.getWorld().dropItem(plr.getLocation(), gm.createGem());
            else
                pi.addItem(gm.createGem());
        } else {
            World plrWorld = plr.getWorld();
            Location plrPos = plr.getLocation();
            for (int i = 0; i < nOfGems; i++) {
                if (pi.firstEmpty() == -1) {
                    plrWorld.dropItem(plrPos, gm.createGem());
                    continue;
                }
                pi.addItem(gm.createGem());
            }
        }
    }
}
