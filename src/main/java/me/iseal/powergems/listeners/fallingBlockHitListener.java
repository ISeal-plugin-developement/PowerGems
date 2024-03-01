package me.iseal.powergems.listeners;

import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class fallingBlockHitListener implements Listener {

    List<String> entityList = new ArrayList<String>();


    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            if (this.containsBlock(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
                this.removeEntityBlock(event.getEntity().getUniqueId());
            }
        }
    }

    public void addEntityUUID(UUID id) {
        String uuid = id.toString();
        this.entityList.add(uuid);
    }

    public void removeEntityBlock(UUID id) {
        String uuid = id.toString();
        if (this.entityList.contains(uuid)) this.entityList.remove(uuid);
    }

    public boolean containsBlock(UUID id) {
        String uuid = id.toString();
        if (this.entityList.contains(uuid)) {
            return true;
        }
        return false;
    }
}
