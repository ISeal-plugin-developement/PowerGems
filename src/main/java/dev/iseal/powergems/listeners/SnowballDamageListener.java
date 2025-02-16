package dev.iseal.powergems.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import dev.iseal.powergems.PowerGems;

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

    @EventHandler 
    public void onSnowmanTarget(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Snowman golem)) { return; }
        if(!(event.getEntity() instanceof Player plr  )) { return; }

        PersistentDataContainer pdc = golem.getPersistentDataContainer();
        String ownerName = pdc.get(
            PowerGems.getPlugin(), "OWNER_NAME", 
            PersistentDataType.STRING);

        if(ownerName != null && ownerName.equals(target.getName())) {
            event.setCancelled(true);}
        }
    }
