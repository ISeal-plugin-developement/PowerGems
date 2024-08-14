package dev.iseal.powergems.listeners.powerListeners;

import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.WrapperObjects.UUIDTagType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class AirListeners implements Listener {

    private static AirListeners instance;
    public static AirListeners getInstance() {
        if (instance == null) {
            instance = new AirListeners();
        }
        return instance;
    }

    NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final HashMap<UUID, Entity> leashEntities = new HashMap<>();

    @EventHandler
    public void onProjLand(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow arrow))
            return;
        if (arrow.getPersistentDataContainer().has(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN) && Objects.equals(arrow.getPersistentDataContainer().get(nkm.getKey("gem_owner"), PersistentDataType.STRING), "Air")) {
            if (!Objects.equals(e.getHitEntity(), null)) {
                Entity hitEntity = e.getHitEntity();
                Entity player = ((Silverfish)leashEntities.get(arrow.getPersistentDataContainer().get(nkm.getKey("leash_entity"), new UUIDTagType()))).getLeashHolder();
                // Calculate direction vector from hitEntity to player
                Vector direction = player.getLocation().toVector().subtract(hitEntity.getLocation().toVector()).normalize();
                double force = 1.5; // Adjust the force as needed
                hitEntity.setVelocity(direction.multiply(force));
            }
            arrow.removePassenger(leashEntities.get(arrow.getPersistentDataContainer().get(nkm.getKey("leash_entity"), new UUIDTagType())));
            arrow.remove();
        }
    }

    public void addLeashEntity(UUID id, Entity entity) {
        leashEntities.put(id, entity);
    }
}
