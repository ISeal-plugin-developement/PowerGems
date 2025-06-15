// Modified AirGemPull class
package dev.iseal.powergems.gems.powerClasses.tasks;

import dev.iseal.powergems.PowerGems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AirGemPull extends BukkitRunnable {
    private final Entity targetEntity;
    private final Player player;
    private final double pullStrength;
    private final int pullDuration;
    private int currentDuration = 0;
    private final int gemLevel;
    private boolean hasAppliedEffects = false;

    public AirGemPull(Entity targetEntity, Player player, double pullStrength, int duration, int gemLevel) {
        this.targetEntity = targetEntity;
        this.player = player;
        this.pullStrength = pullStrength;
        this.pullDuration = duration;
        this.gemLevel = gemLevel;
    }

    public void start() {
        if (this.targetEntity != null) {
            runTaskTimer(PowerGems.getPlugin(), 0, 1);
        }
    }

    @Override
    public void run() {
        if (targetEntity == null ||
                targetEntity.isDead() ||
                !targetEntity.isValid() ||
                !player.isOnline() ||
                currentDuration >= pullDuration) {
            cancel();
            return;
        }

        // Check if target is within 2 blocks of the player
        if (targetEntity.getLocation().distance(player.getLocation()) < 2) {
            if (!hasAppliedEffects) {
                applyEffects();
                hasAppliedEffects = true;
            }
            cancel();
            return;
        }

        // Calculate pull direction
        Location playerLocation = player.getLocation();
        Vector pullDirection = playerLocation.toVector()
                .subtract(targetEntity.getLocation().toVector())
                .normalize()
                .multiply(pullStrength);

        // Apply pull velocity to target entity
        targetEntity.setVelocity(pullDirection);

        // Create beam particle effect between the players
        drawBeam(targetEntity.getLocation(), playerLocation);

        currentDuration++;
    }

    private void drawBeam(Location from, Location to) {
        Vector direction = to.clone().subtract(from).toVector().normalize();
        double distance = from.distance(to);

        // Create a particle beam with spacing of 0.5 blocks
        for (double d = 0; d < distance; d += 0.5) {
            Location particleLoc = from.clone().add(direction.clone().multiply(d));

            // Create a blue/white dust particle for the beam effect
            DustOptions dustOptions = new DustOptions(Color.fromRGB(173, 216, 230), 1.0f);
            from.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dustOptions);
        }
    }

    private void applyEffects() {
        // Apply effects when the target reaches the player
        if (targetEntity instanceof LivingEntity) {
            ((LivingEntity) targetEntity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * gemLevel, 1));
        }
    }
}