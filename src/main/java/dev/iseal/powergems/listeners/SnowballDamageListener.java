package dev.iseal.powergems.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SnowballDamageListener implements Listener {

@EventHandler
public void onSnowmanHit(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Snowball snowball)) {
        return;
    }
    if (!(snowball.getShooter() instanceof Snowman golem)) {
        return;
    }
    // Check if this golem was spawned by Ice Gem
    PersistentDataContainer golemPDC = golem.getPersistentDataContainer();
    if (!golemPDC.has(new NamespacedKey(dev.iseal.powergems.PowerGems.getPlugin(), "SNOWBALL_DAMAGE"), 
                    PersistentDataType.DOUBLE)) {
        return;
    }
    // Apply the damage buff
    Double damage = golemPDC.get(
        new NamespacedKey(dev.iseal.powergems.PowerGems.getPlugin(), "SNOWBALL_DAMAGE"),
        PersistentDataType.DOUBLE
    );
    if (damage != null) {
        event.setDamage(damage);
    }
}
}
