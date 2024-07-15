package me.iseal.powergems.listeners.powerListeners;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.NamespacedKeyManager;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

public class IronProjectileLandListener implements Listener {

    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    @EventHandler
    public void onProjLand(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getEntity();
            if (arrow.getPersistentDataContainer() != null) {
                if (arrow.getPersistentDataContainer().has(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN)) {
                    arrow.remove();
                }
            }
        }
    }

}
