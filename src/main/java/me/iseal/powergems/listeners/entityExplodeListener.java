package me.iseal.powergems.listeners;

import me.iseal.powergems.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataType;

public class entityExplodeListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent e){
        if (!e.getEntity().getPersistentDataContainer().has(Main.getIsGemExplosionKey(), PersistentDataType.BOOLEAN)){
            return;
        }
        if (!e.getEntity().getPersistentDataContainer().get(Main.getIsGemExplosionKey(), PersistentDataType.BOOLEAN)){
            return;
        }
        e.setCancelled(true);
    }
}
