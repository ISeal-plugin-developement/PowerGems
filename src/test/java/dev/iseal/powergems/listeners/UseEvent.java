package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.Configuration.ActiveGemsConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UseEvent implements Listener {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final TempDataManager tdm = sm.tempDataManager;
    private final ActiveGemsConfigManager agcm = sm.configManager.getRegisteredConfigInstance(ActiveGemsConfigManager.class);
    private final GemManager gm = sm.gemManager;

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack item = null;
        if (gm.isGem(offHandItem)) {
            item = offHandItem;
        } else if (gm.isGem(mainHandItem)) {
            item = mainHandItem;
        } else {
            return;
        }

        if (tdm.cantUseGems.containsKey(player)) {
            if (System.currentTimeMillis() < tdm.cantUseGems.get(player)) {
                player.sendMessage(ChatColor.DARK_RED + "You can't use gems for another " + (tdm.cantUseGems.get(player) - System.currentTimeMillis()) / 1000 + " seconds!");
                return;
            } else {
                tdm.cantUseGems.remove(player);
            }
        }
        Action action = e.getAction();
        handlePower(player, action, item);
    }

    private void handlePower(Player p, Action a, ItemStack item) {
        if (agcm.isGemActive(gm.getGemName(item))) {
            gm.runCall(item, a, p);
        } else {
            p.sendMessage(ChatColor.DARK_RED + "That gem is disabled!");
            p.sendMessage(ChatColor.DARK_RED + "Ask the server manager if you think it should be activated.");
        }
    }

}
