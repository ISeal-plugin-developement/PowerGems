package dev.iseal.powergems.listeners.powerListeners;

import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.LinkedList;

public class StrenghtMoveListener implements Listener {
    public static final double RADIUS = 5.0;
    public static final double RADIUS_SQUARED = RADIUS * RADIUS;
    private final LinkedList<Location> startingLocations = new LinkedList<>();

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) { //NOPMD - This is a listener.
        for (Location startingLocation : startingLocations) {
            Location from = event.getFrom();
            Location to = event.getTo();

            if (startingLocation.getWorld() != to.getWorld()) {
                return;
            }
            double toDistance = to.distanceSquared(startingLocation);
            if (toDistance > 2*RADIUS_SQUARED) {
                return; // clearly has nothing to do with us, ignore it.
            }
            double fromDistance = from.distanceSquared(startingLocation);
            boolean toInside = toDistance < RADIUS_SQUARED;
            boolean fromInside = fromDistance < RADIUS_SQUARED;
            if (toInside ^ fromInside) {
                event.setCancelled(true);
            }
        }
    }

    public void addStartingLocation(Location l) {
        if (!startingLocations.contains(l)) {
            startingLocations.add(l);
        }
    }

    public void removeStartingLocation(Location l) {
        startingLocations.remove(l);
    }
}
