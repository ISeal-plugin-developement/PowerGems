package me.iseal.powergems.listeners;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.GemManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Random;

public class inventoryCloseListener implements Listener {

    private final ItemStack randomGem = Main.getSingletonManager().gemManager.getRandomGemItem();
    private final GemManager gm = Main.getSingletonManager().gemManager;
    private final Random rand = new Random();

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if (!(e.getInventory().getHolder() instanceof Player plr)) {
            return;
        }
        if (!(e.getView().getBottomInventory() instanceof PlayerInventory pi)) {
            return;
        }
        //checkIfMultipleGems(plr);
        if (!e.getView().getBottomInventory().containsAtLeast(randomGem, 1)){
            return;
        }
        int nOfGems = 0;
        int intAt = -1;
        for (ItemStack item : pi.getContents()){
            intAt++;
            if (item == null || !item.isSimilar(randomGem)){
                continue;
            }
            nOfGems+= item.getAmount();
            pi.setItem(intAt, null);
        }
        if (Main.config.getBoolean("allowOnlyOneGem")){
            pi.addItem(gm.createGem());
        } else {
            World plrWorld = plr.getWorld();
            Location plrPos = plr.getLocation();
            for (int i = 0; i < nOfGems; i++) {
                if (pi.firstEmpty() == -1){
                    plrWorld.dropItem(plrPos, gm.createGem());
                    continue;
                }
                pi.addItem(gm.createGem());
            }
        }
    }

    private void checkIfMultipleGems(Player plr){
        if (!Main.config.getBoolean("allowOnlyOneGem")){
            return;
        }
        HashMap<ItemStack, Integer> gems = new HashMap<>(3);
        final PlayerInventory plrInv = plr.getInventory();
        int index = 0;
        for (ItemStack i : plrInv.getContents()){
            if (gm.isGem(i)){
                i.setAmount(1);
                gems.put(i, index);
                plrInv.setItem(index, null);
            }
            index++;
        }
        if (gems.isEmpty()){
            return;
        }
        if (gems.size() == 1){
            ItemStack firstGem = gems.keySet().stream().findFirst().get();
            if (gems.get(firstGem) == -1){
                plrInv.setItemInOffHand(firstGem);
                return;
            }
            plrInv.setItem(gems.get(firstGem), firstGem);
            return;
        }
        ItemStack randomGem = gems.keySet().stream().skip(rand.nextInt(gems.size())).findFirst().get();
        if (gems.get(randomGem) == -1){
            plrInv.setItemInOffHand(randomGem);
            return;
        }
        plrInv.setItem(gems.get(randomGem), randomGem);
    }

}
