package dev.iseal.powergems.listeners.powerListeners;

import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.LinkedList;

public class StrenghtMoveListener implements Listener {
    private final double radius = 5.0;
    private final LinkedList<Location> startingLocations = new LinkedList<>();

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) { //NOPMD - This is a listener.
        for (Location startingLocation : startingLocations) {
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();

            if (startingLocation.getWorld() != to.getWorld()) {
                return;
            }
            if (to.distance(startingLocation) > radius && from.distance(startingLocation) > 6) {
                return;
            } else if (to.distance(startingLocation) < radius) {
                if (from.distance(startingLocation) < radius) {
                    return;
                } else {
                    event.setCancelled(true);
                    player.teleport(from);
                    player.sendMessage(I18N.translate("CANNOT_ENTER_ARENA"));
                    return;
                }
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
