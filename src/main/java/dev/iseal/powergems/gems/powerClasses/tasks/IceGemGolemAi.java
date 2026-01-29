package dev.iseal.powergems.gems.powerClasses.tasks;

import dev.iseal.powergems.PowerGems;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class IceGemGolemAi implements Listener {

@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
public void onSnowmanHit(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Snowball snowball && 
        snowball.getShooter() instanceof Snowman golem)) {
        return;
    }

    // Prevent damage to other snow golems
    if (event.getEntity() instanceof Snowman) {
        event.setCancelled(true);
        return;
    }

    // Apply custom damage to other entities
    PersistentDataContainer golemPDC = golem.getPersistentDataContainer();
    NamespacedKey damageKey = new NamespacedKey(PowerGems.getPlugin(), "SNOWBALL_DAMAGE");
    
    Double damage = golemPDC.get(damageKey, PersistentDataType.DOUBLE);
    if (damage != null) {
        event.setDamage(damage);
    }
}

@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
public void onSnowmanTarget(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Snowman golem && 
        event.getDamager() instanceof Player player)) {
        return;
    }

    NamespacedKey ownerKey = new NamespacedKey(PowerGems.getPlugin(), "OWNER_NAME");
    String ownerName = golem.getPersistentDataContainer()
                        .get(ownerKey, PersistentDataType.STRING);

    if (player.getName().equals(ownerName)) {
        event.setCancelled(true);
    }
}
}
