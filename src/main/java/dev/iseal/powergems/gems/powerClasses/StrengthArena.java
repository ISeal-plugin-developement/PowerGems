package dev.iseal.powergems.gems.powerClasses;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static dev.iseal.powergems.listeners.powerListeners.StrenghtMoveListener.RADIUS;
import static dev.iseal.powergems.listeners.powerListeners.StrenghtMoveListener.RADIUS_SQUARED;

public class StrengthArena implements Listener {

    private final SingletonManager sm = SingletonManager.getInstance();

    private final Player player;
    private final Location startingLocation;
    private final int level;

    public StrengthArena(Player player, int level) {
        this.player = player;
        this.startingLocation = player.getLocation();
        this.level = level;
    }

    public void start() {
        if (startingLocation == null) {
            throw new IllegalStateException("StartingLocation is null. Ensure the player is not null before calling start().");
        }
        Vector center = startingLocation.toVector();
        sm.strenghtMoveListener.addStartingLocation(startingLocation);
        new BukkitRunnable() {
            int currentTime = 0;
            final int length = (level*level)*2;

            public void run() {
                if (currentTime >= length) {
                    sm.strenghtMoveListener.removeStartingLocation(startingLocation);
                    cancel();
                    return;
                }
                int latSteps = 16; // Number of latitude steps
                int lonSteps = 32; // Number of longitude steps
                for (int i = 0; i <= latSteps; i++) {
                    double lat = Math.PI * i / latSteps;
                    double y = center.getY() + RADIUS * Math.cos(lat);
                    double r = RADIUS * Math.sin(lat);
                    for (int j = 0; j < lonSteps; j++) {
                        double lon = 2 * Math.PI * j / lonSteps;
                        double x = center.getX() + r * Math.cos(lon);
                        double z = center.getZ() + r * Math.sin(lon);
                        Location particleLocation = new Location(startingLocation.getWorld(), x, y, z);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, particleLocation, 2, 0.01, 0.01, 0.01, 0.01);
                    }
                }

                Bukkit.getOnlinePlayers().stream()
                        .filter(candidate -> !candidate.equals(player))
                        .filter(candidate -> candidate.getLocation().distanceSquared(startingLocation) < RADIUS_SQUARED)
                        .forEach( candidate -> {
                            candidate.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 15, (level - 1 )/2));
                            candidate.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 15, (level - 1 )/2));
                        });

                /*
                // Push player away from the sphere if not grounded
                if (!player.isOnGround()) {
                    Vector playerPos = player.getLocation().toVector();
                    Vector fromCenter = playerPos.clone().subtract(center);
                    if (fromCenter.lengthSquared() > 0.01) {
                        Vector pushDir = fromCenter.normalize();
                        double pushStrength = 0.3;
                        pushDir.setY(player.getVelocity().getY());
                        player.setVelocity(pushDir.multiply(pushStrength));
                    }
                }*/
                currentTime++;
            }
        }.runTaskTimer(PowerGems.getPlugin(), 0L, 10L);
    }
}
