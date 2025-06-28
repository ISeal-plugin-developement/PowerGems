package dev.iseal.powergems.gems.powerClasses;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StrengthArena implements Listener {

    private final SingletonManager sm = SingletonManager.getInstance();

    private final Player player;
    private final Location StartingLocation;
    private final int radius = 5;
    private final int particleCount = 40;

    public StrengthArena(Player player) {
        this.player = player;
        if (player == null) {
            this.StartingLocation = null;
        } else {
            this.StartingLocation = player.getLocation();
        }
    }

    public void start() {
        if (StartingLocation == null) {
            throw new IllegalStateException("StartingLocation is null. Ensure the player is not null before calling start().");
        }
        Vector center = StartingLocation.toVector();
        sm.strenghtMoveListener.addStartingLocation(StartingLocation);
        new BukkitRunnable() {
            int currentTime = 0;

            public void run() {
                if (currentTime >= 20) {
                    sm.strenghtMoveListener.removeStartingLocation(StartingLocation);
                    cancel();
                    return;
                }
                double angle = 2 * Math.PI / particleCount;
                for (int i = 0; i < particleCount; i++) {
                    double x = center.getX() + radius * Math.cos(angle * i);
                    double y = center.getY();
                    double z = center.getZ() + radius * Math.sin(angle * i);
                    Location particleLocation = new Location(StartingLocation.getWorld(), x, y, z);
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, particleLocation, 2);
                }
                currentTime++;
            }
        }.runTaskTimer(PowerGems.getPlugin(), 0L, 10L);
    }
}
