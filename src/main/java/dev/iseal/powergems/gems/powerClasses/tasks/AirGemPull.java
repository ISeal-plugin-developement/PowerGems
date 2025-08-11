package dev.iseal.powergems.gems.powerClasses.tasks;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AirGemPull {
    private final Entity entity;
    private final Player player;
    private final double pullStrength;
    private final int pullDuration;
    private int currentDuration = 0;

    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    public AirGemPull(Entity entity, Player player, double pullStrength, int duration) {
        this.entity = entity;
        this.player = player;
        this.pullStrength = pullStrength;
        this.pullDuration = duration;
    }

    public void start() {
        schedulerWrapper.scheduleRepeatingTaskForEntity(entity, this::run, 0, 1, null);
    }

    public void run() {
        if (entity.isDead()
                || !entity.isValid()
                || !player.isOnline()
                || entity.getLocation().distance(player.getLocation()) < 2
                || currentDuration >= pullDuration
        ) {
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