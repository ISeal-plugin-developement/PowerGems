package dev.iseal.powergems.gems.powerClasses.tasks;

import dev.iseal.powergems.PowerGems;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AirGemPull extends BukkitRunnable{
    private final Entity entity;
    private final Player player;
    private final double pullStrength;
    private final int pullDuration;
    private int currentDuration = 0;

    public AirGemPull(Entity entity, Player player, double pullStrength, int duration) {
        this.entity = entity;
        this.player = player;
        this.pullStrength = pullStrength;
        this.pullDuration = duration;
    }

    public void start() {
        runTaskTimer(PowerGems.getPlugin(), 0, 1);
    }

    @Override
    public void run() {
        if (entity.isDead()
                || !entity.isValid()
                || !player.isOnline()
                || entity.getLocation().distance(player.getLocation()) < 2
                || currentDuration >= pullDuration
        ) {
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
        currentDuration++;
    }
}