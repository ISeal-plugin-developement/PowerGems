package me.iseal.powergems.listeners.powerListeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.LinkedList;

public class strenghtMoveListener implements Listener {
    private final double radius = 5.0;
    private LinkedList<Location> startingLocations = new LinkedList<>();

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        for (Location startingLocation : startingLocations) {
            Player player = event.getPlayer();
            Location from = event.getFrom();
            Location to = event.getTo();

            if (startingLocation.getWorld() != to.getWorld()) {
                return;
            }
            if (to.distance(startingLocation) > radius && from.distance(startingLocation) > 6) {
                return;
            }
            else if (to.distance(startingLocation) < radius) {
                if (from.distance(startingLocation) < radius) {
                    return;
                }
                else {
                    event.setCancelled(true);
                    player.teleport(from);
                    player.sendMessage(ChatColor.DARK_RED+"You cannot enter the Arena!");
                    return;
                }
            }
        }
    }

    public void addStartingLocation(Location l){
        if (!startingLocations.contains(l)){
            startingLocations.add(l);
        }
    }

    public void removeStartingLocation(Location l){
        if (startingLocations.contains(l)){
            startingLocations.remove(l);
        }
    }
}
