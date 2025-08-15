package dev.iseal.powergems.gems.powerClasses;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class StrengthArena {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;
    private final Location startingLocation;
    private final Player player;
    private final int radius = 5;

    public StrengthArena(Player player) {
        if (player == null) {
            this.startingLocation = null;
            this.player = null;
        } else {
            this.startingLocation = player.getLocation();
            this.player = player;
        }
    }

    public void start() {
        if (startingLocation == null) {
            throw new IllegalStateException("StartingLocation is null. Ensure the player is not null before calling start().");
        }
        Vector center = startingLocation.toVector();
        sm.strenghtMoveListener.addStartingLocation(startingLocation);

        schedulerWrapper.scheduleRepeatingTaskAtLocation(startingLocation, new Runnable() {
            int currentTime = 0;

            public void run() {
                if (currentTime >= 20) {
                    sm.strenghtMoveListener.removeStartingLocation(startingLocation);
                    return;
                }
                int latSteps = 16; // Number of latitude steps
                int lonSteps = 32; // Number of longitude steps
                for (int i = 0; i <= latSteps; i++) {
                    double lat = Math.PI * i / latSteps;
                    double y = center.getY() + radius * Math.cos(lat);
                    double r = radius * Math.sin(lat);
                    for (int j = 0; j < lonSteps; j++) {
                        double lon = 2 * Math.PI * j / lonSteps;
                        double x = center.getX() + r * Math.cos(lon);
                        double z = center.getZ() + r * Math.sin(lon);
                        Location particleLocation = new Location(startingLocation.getWorld(), x, y, z);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, particleLocation, 2, 0.01, 0.01, 0.01, 0.01);
                    }
                }

                // Push player away from the sphere if not grounded
                Location playerLoc = player.getLocation();
                Location belowPlayer = playerLoc.clone().subtract(0, 0.1, 0);
                boolean isPlayerOnGround = belowPlayer.getBlock().getType().isSolid();

                if (!isPlayerOnGround) {
                    Vector playerPos = player.getLocation().toVector();
                    Vector fromCenter = playerPos.clone().subtract(center);
                    if (fromCenter.lengthSquared() > 0.01) {
                        Vector pushDir = fromCenter.normalize();
                        double pushStrength = 0.3;
                        pushDir.setY(player.getVelocity().getY());
                        player.setVelocity(pushDir.multiply(pushStrength));
                    }
                }
                currentTime++;
            }
        }, 0L, 10L);
    }
}
