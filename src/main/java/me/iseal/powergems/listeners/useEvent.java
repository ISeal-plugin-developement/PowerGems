package me.iseal.powergems.listeners;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.ConfigManager;
import me.iseal.powergems.managers.GemManager;
import me.iseal.powergems.managers.SingletonManager;
import me.iseal.powergems.managers.TempDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class useEvent implements Listener {

    private SingletonManager sm = Main.getSingletonManager();
    private TempDataManager tdm = sm.tempDataManager;
    private ConfigManager cm = sm.configManager;
    private GemManager gm = sm.gemManager;


    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack item = null;
        if (offHandItem.getType() == Material.EMERALD && offHandItem.hasItemMeta()){
            item = offHandItem;
        } else if (mainHandItem.getType() == Material.EMERALD && mainHandItem.hasItemMeta()) {
            item = mainHandItem;
        } else {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (dataContainer.has(Main.getIsGemKey(), PersistentDataType.BOOLEAN) && dataContainer.has(Main.getGemPowerKey(), PersistentDataType.STRING)) {
            if (tdm.cantUseGems.containsKey(player)) {
                if (System.currentTimeMillis() < tdm.cantUseGems.get(player)) {
                    player.sendMessage(ChatColor.DARK_RED + "You can't use gems for another " + (tdm.cantUseGems.get(player) - System.currentTimeMillis()) / 1000 + " seconds!");
                    return;
                } else {
                    tdm.cantUseGems.remove(player);
                }
            }
            if (!item.getItemMeta().hasCustomModelData()){
                Bukkit.getLogger().info("Found legacy gem! Migrating...");
                meta.setCustomModelData(dataContainer.get(Main.getGemPowerKey(), PersistentDataType.INTEGER));
                item.setItemMeta(meta);
                Bukkit.getLogger().info("Done!");
            }
            Action action = e.getAction();
            handlePower(player, action, item);
        }
    }

    private void handlePower(Player p, Action a, ItemStack item){
        if (cm.isGemActive(gm.getGemName(item))){
            gm.runCall(item, a, p);
        } else {
            p.sendMessage(ChatColor.DARK_RED+"That gem is disabled!");
            p.sendMessage(ChatColor.DARK_RED+"Ask the server manager if you think it should be activated.");
        }
    }

}
