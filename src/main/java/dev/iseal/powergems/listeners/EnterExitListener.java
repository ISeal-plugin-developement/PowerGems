package dev.iseal.powergems.listeners;

import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import de.leonhard.storage.Json;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.gems.IronGem;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;

public class EnterExitListener implements Listener {

    private final Json playerJoined = new Json("playerData", PowerGems.getPlugin().getDataFolder().getPath()+"\\data\\");
    private final SingletonManager sm = SingletonManager.getInstance();
    private final IronGem ironGem = new IronGem();
    private final GemManager gm = sm.gemManager;
    private final TempDataManager tdm = sm.tempDataManager;
    private final GeneralConfigManager cm = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
    private final long delay = cm.getDelayToUseGems() * 1000;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player plr = e.getPlayer();
        checkIfRemovePowers(plr);
        addDelay(plr);
        giveGemOnFirstLogin(plr);
    }

    private void giveGemOnFirstLogin(Player plr) {
        if (!cm.getGiveGemOnFirstLogin()) {
            return;
        }
        UUID playerUUID = plr.getUniqueId();
        if (!playerJoined.contains(String.valueOf(playerUUID))) {
            playerJoined.set(String.valueOf(playerUUID), System.currentTimeMillis());
            plr.getInventory().addItem(gm.createGem());
        }
    }

    private void addDelay(Player plr) {
        tdm.cantUseGems.remove(plr);
        // add delay
        tdm.cantUseGems.put(plr, System.currentTimeMillis() + delay);
        LinkedList<ItemStack> gems = new LinkedList<>();
        for (ItemStack i : plr.getInventory().getContents()) {
            if (gm.isGem(i)) {
                gems.add(i);
            }
        }
    }

    private void checkIfRemovePowers(Player plr) {
        if (tdm.ironShiftLeft.contains(plr.getUniqueId())) {
            ironGem.removeShiftModifiers(plr);
            tdm.ironShiftLeft.remove(plr.getUniqueId());
        }
        if (tdm.ironRightLeft.contains(plr.getUniqueId())) {
            ironGem.removeRightModifiers(plr);
            tdm.ironRightLeft.remove(plr.getUniqueId());
        }
    }

}
