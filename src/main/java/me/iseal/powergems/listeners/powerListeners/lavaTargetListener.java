package me.iseal.powergems.listeners.powerListeners;

import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.ArrayList;

public class lavaTargetListener implements Listener {

    ArrayList<Player> lavaNoTargetList = new ArrayList<>();

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e){
        if (lavaNoTargetList.size() > 0){
            Entity targeter = e.getEntity();
            if (targeter instanceof Blaze){
                if (lavaNoTargetList.contains(e.getTarget())){
                    e.setCancelled(true);
                }
            }
        }
    }

    public void addToList(Player plr){
        lavaNoTargetList.add(plr);
    }

    public void removeFromList(Player plr){
        lavaNoTargetList.remove(plr);
    }

}
