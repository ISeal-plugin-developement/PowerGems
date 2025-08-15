package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class EntityDamageListener implements Listener {

    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damageCause = event.getDamager();
        PersistentDataContainer pdc = damageCause.getPersistentDataContainer();
        if (!pdc.has(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN)) {
            return; // if not gem explosion, do nothing
        }
        if (!pdc.has(nkm.getKey("projectile_sender"), PersistentDataType.STRING)) {
            return; // if we don't have a sender, do nothing
        }
        UUID senderUUID = UUID.fromString(pdc.get(nkm.getKey("projectile_sender"), PersistentDataType.STRING));
        Entity damaged = event.getEntity();
        if (damaged.getUniqueId().equals(senderUUID)) {
            // if the sender is the same as the damaged entity, cancel the damage
            event.setCancelled(true);
        }
        // if the sender is not the same as the damaged entity, do nothing
    }

}
