package me.iseal.powergems.listeners.powerListeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class sandMoveListener implements Listener {

    private ArrayList<Block> slowSandList = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if (slowSandList.size() > 0){
            Player p = e.getPlayer();
            Location to = e.getTo();
            for (Block l : slowSandList){
                if (l.getWorld() != to.getWorld()){
                    continue;
                }
                if (to.distance(l.getLocation()) < 1.5){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2));
                }
            }
        }
    }

    public void addToList(Block l){
        slowSandList.add(l);
    }

    public void removeFromList(Block l){
        slowSandList.remove(l);
    }

    public boolean hasBlock(Block block){
        return slowSandList.contains(block);
    }

}
