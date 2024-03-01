package me.iseal.powergems.listeners.powerListeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.ArrayList;

public class iceTargetListener implements Listener {

    ArrayList<Player> iceNoTargetList = new ArrayList<>();

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e){
        if (iceNoTargetList.size() > 0){
            Entity targeter = e.getEntity();
            if (targeter instanceof Snowman){
                if (iceNoTargetList.contains(e.getTarget())){
                    e.setCancelled(true);
                }
            }
        }
    }

    public void addToList(Player plr){
        iceNoTargetList.add(plr);
    }

    public void removeFromList(Player plr){
        iceNoTargetList.remove(plr);
    }

}
