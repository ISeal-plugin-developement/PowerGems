package me.iseal.powergems.listeners.powerListeners;

import me.iseal.powergems.Main;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

public class ironProjectileLandListener implements Listener {

    @EventHandler
    public void onProjLand(ProjectileHitEvent e){
        if (e.getEntity() instanceof Arrow){
            Arrow arrow = (Arrow) e.getEntity();
            if (arrow.getPersistentDataContainer() != null){
                if (arrow.getPersistentDataContainer().has(Main.getIsGemProjectileKey(), PersistentDataType.BOOLEAN)){
                    arrow.remove();
                }
            }
        }
    }

}
