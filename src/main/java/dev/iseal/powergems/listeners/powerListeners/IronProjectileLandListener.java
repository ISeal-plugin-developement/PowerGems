package dev.iseal.powergems.listeners.powerListeners;

import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class IronProjectileLandListener implements Listener {

    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    @EventHandler
    public void onProjLand(ProjectileHitEvent e) {
        if (
                e.getEntity() instanceof Arrow arrow
        ) {
            arrow.getPersistentDataContainer();
            if (arrow.getPersistentDataContainer().has(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN) && Objects.equals(arrow.getPersistentDataContainer().get(nkm.getKey("gem_owner"), PersistentDataType.STRING), "Iron")) {
                arrow.remove();
            }
        }
    }

}
