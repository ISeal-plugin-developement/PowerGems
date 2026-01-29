package dev.iseal.powergems.listeners;

import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.HashMap;
import java.util.UUID;

public class AvoidTargetListener implements Listener {

    private static AvoidTargetListener avoidTargetListener;
    public static AvoidTargetListener getInstance() {
        if (avoidTargetListener == null) {
            avoidTargetListener = new AvoidTargetListener();
        }
        return avoidTargetListener;
    }

    // Private constructor to prevent instantiation
    private AvoidTargetListener() {}

    private final HashMap<UUID, LivingEntity> avoidTargetList = new HashMap<>();

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() == null) {
            return;
        }
        LivingEntity target = e.getTarget();
        Entity targeter = e.getEntity();
        cleanupList();
        if (!avoidTargetList.containsKey(target.getUniqueId())) {
            return;
        }
        if (avoidTargetList.get(target.getUniqueId()).equals(targeter)) {
            e.setCancelled(true);
        }
    }

    private void cleanupList() {
        avoidTargetList.entrySet().removeIf(entry -> {
            LivingEntity target = entry.getValue();
            return target == null || target.isDead() || !target.isValid(); // Remove the entry if the target is dead or invalid
        });
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();
        if (!avoidTargetList.containsKey(damaged.getUniqueId())) {
            return;
        }
        if (avoidTargetList.get(damaged.getUniqueId()).equals(damager)) {
            damaged.sendMessage(I18N.translate("DAMAGE_AVOIDED_PROTECTION").replace("{damager_name}", damager.getName()));
            e.setCancelled(true);
        }
    }


    /*
        * Add the player and its target to the list
        * @param plr The player to add to the list
        * @param target The target that will avoid the player
     */
    public void addToList(Player plr, LivingEntity target) {
        avoidTargetList.put(plr.getUniqueId(), target);
    }
}
