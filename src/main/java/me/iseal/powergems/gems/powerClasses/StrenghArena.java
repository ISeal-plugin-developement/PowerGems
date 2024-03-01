package me.iseal.powergems.gems.powerClasses;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class StrenghArena implements Listener {

    SingletonManager sm = Main.getSingletonManager();

    private Player player;
    private Location StartingLocation;
    private final int radius = 5;
    private final int particleCount = 40;

    public StrenghArena(Player player) {
        this.player = player;
        if (player == null){
            this.StartingLocation = null;
        } else {
            this.StartingLocation = player.getLocation();
        }
    }

    public void start() {
        Vector center = StartingLocation.toVector();
        sm.strenghtMoveListen.addStartingLocation(StartingLocation);
        new BukkitRunnable() {
            int currentTime = 0;
            public void run() {
                if (currentTime >= 20) {
                    sm.strenghtMoveListen.removeStartingLocation(StartingLocation);
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
        }.runTaskTimer(Main.getPlugin(), 0L, 10L);
    }
}
