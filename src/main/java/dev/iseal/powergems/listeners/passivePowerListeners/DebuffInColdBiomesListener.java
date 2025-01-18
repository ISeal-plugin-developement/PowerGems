package dev.iseal.powergems.listeners.passivePowerListeners;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class DebuffInColdBiomesListener implements Listener {

    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final List<String> allowedGems = List.of("Fire", "Lava");
    private final ArrayBlockingQueue<UUID> slowedPlayers = new ArrayBlockingQueue<>(5);

    private boolean playerHasAllowedGem(Player plr) {
        return gm.getPlayerGems(plr).stream()
                .anyMatch(i -> allowedGems.contains(i.getItemMeta().getPersistentDataContainer().get(nkm.getKey("gem_power"), PersistentDataType.STRING)));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlock() == e.getTo().getBlock()) {
            return;
        }
        if (slowedPlayers.contains(e.getPlayer().getUniqueId())) {
            return;
        }
        Player plr = e.getPlayer();
        if (!playerHasAllowedGem(plr))
            return;
        if (plr.getLocation().getBlock().getTemperature() <= 0.05) {
            utils.addPreciseEffect(plr, PotionEffectType.SLOW, 60, 1);
            slowedPlayers.add(plr.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    slowedPlayers.remove(plr.getUniqueId());
                }
            }.runTaskLater(PowerGems.getPlugin(), 60);
        }
    }
}