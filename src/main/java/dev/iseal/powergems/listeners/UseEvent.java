package dev.iseal.powergems.listeners;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Configuration.ActiveGemsConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.royawesome.jlibnoise.module.combiner.Power;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UseEvent implements Listener {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final TempDataManager tdm = sm.tempDataManager;
    private final ActiveGemsConfigManager agcm = sm.configManager.getRegisteredConfigInstance(ActiveGemsConfigManager.class);
    private final GemManager gm = sm.gemManager;

    // hacky fix for double trigger
    private final List<UUID> recentlyUsed = new ArrayList<>();

    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        if (recentlyUsed.contains(e.getPlayer().getUniqueId())) return;
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

        recentlyUsed.add(e.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLater(PowerGems.getPlugin(), () -> recentlyUsed.remove(e.getPlayer().getUniqueId()), 1);

        if (tdm.cantUseGems.containsKey(player)) {
            if (System.currentTimeMillis() < tdm.cantUseGems.get(player)) {
                player.sendMessage(I18N.translate("ON_COOLDOWN_GEMS").replace("{time}",
                        String.valueOf((tdm.cantUseGems.get(player) - System.currentTimeMillis()) / 1000)));
                return;
            } else {
                tdm.cantUseGems.remove(player);
            }
        }
        Action action = e.getAction();
        handlePower(player, action, item);
    }

    private void handlePower(Player p, Action a, ItemStack item) {
        if (agcm.isGemActive(gm.getName(item))) {
            gm.runCall(item, a, p);
        } else {
            p.sendMessage(I18N.translate("GEM_DISABLED"));
        }
    }

}
