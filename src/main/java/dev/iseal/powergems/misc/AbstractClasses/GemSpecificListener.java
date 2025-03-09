package dev.iseal.powergems.misc.AbstractClasses;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class GemSpecificListener implements Listener {

    private final int effectLength;
    private final List<String> allowedGems;
    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final ArrayBlockingQueue<UUID> activePlayers = new ArrayBlockingQueue<>(5);

    public GemSpecificListener(List<String> allowedGems, int effectLength) {
        this.allowedGems = allowedGems;
        this.effectLength = effectLength;
    }

    private int playerAllowedGemsCount(Player plr) {
        return (int) gm.getPlayerGems(plr).stream()
                .filter(Objects::nonNull)
                .filter(ItemStack::hasItemMeta)
                .filter(i -> i.getItemMeta() != null)
                .filter(i -> allowedGems.contains(i.getItemMeta().getPersistentDataContainer().get(nkm.getKey("gem_power"), PersistentDataType.STRING)))
                .count();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlock() == e.getTo().getBlock()) {
            return;
        }
        if (activePlayers.contains(e.getPlayer().getUniqueId())) {
            return;
        }
        Player plr = e.getPlayer();
        int allowedGems = playerAllowedGemsCount(plr);
        if (allowedGems == 0)
            return;
        if (!applyEffect(e, allowedGems))
            return;
        activePlayers.add(plr.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                activePlayers.remove(plr.getUniqueId());
            }
        }.runTaskLater(PowerGems.getPlugin(), effectLength);
    }

    /*
        * This method is used to apply the effect to the player
        * @param e The PlayerMoveEvent
        * @return Whether the effect was applied or not
     */
    public abstract boolean applyEffect(PlayerMoveEvent e, int allowedGemNumber);
}
