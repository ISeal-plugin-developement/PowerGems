package me.iseal.powergems.listeners;

import me.iseal.powergems.Main;
import me.iseal.powergems.listeners.powerListeners.SandMoveListener;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class NoGemHittingListener implements Listener {

    private final SandMoveListener sml = SingletonManager.getInstance().sandMoveListen;

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (sml.hasBlock(e.getBlock())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player)
            return;
        if (!(e.getDamager() instanceof Player))
            return;
        if (e.getEntity().getPersistentDataContainer() == null)
            return;
        if (!e.getEntity().getPersistentDataContainer().has(Main.getIsGemProjectileKey(), PersistentDataType.BOOLEAN))
            return;
        e.setCancelled(true);
    }

}
