package dev.iseal.powergems.gems.powerClasses.tasks;

import dev.iseal.powergems.PowerGems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class AirGemPull {
    private final Entity entity;
    private final Player player;
    private final double pullStrength;
    private BukkitTask task;

    public AirGemPull(Entity entity, Player player, double pullStrength) {
        this.entity = entity;
        this.player = player;
        this.pullStrength = pullStrength;
    }

    public void startPulling() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead() || !entity.isValid() || !player.isOnline() ||
                        entity.getLocation().distance(player.getLocation()) < 2) {
                    cancel();
                    return;
                }

                // Calculate pull direction
                Location playerLocation = player.getLocation();
                Vector pullDirection = playerLocation.toVector()
                        .subtract(entity.getLocation().toVector())
                        .normalize()
                        .multiply(pullStrength);

                entity.setVelocity(pullDirection);
            }
        }.runTaskTimer(PowerGems.getPlugin(), 0L, 1L);
    }

    public void cancel() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}