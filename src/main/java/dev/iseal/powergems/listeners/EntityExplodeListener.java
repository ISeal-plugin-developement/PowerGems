package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;

public class EntityExplodeListener implements Listener {

    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (!e.getEntity().getPersistentDataContainer().has(nkm.getKey("is_gem_explosion"), PersistentDataType.BOOLEAN)) {
            return;
        }
        if (!e.getEntity().getPersistentDataContainer().get(nkm.getKey("is_gem_explosion"), PersistentDataType.BOOLEAN)) {
            return;
        }
        e.setCancelled(true);
    }
}
