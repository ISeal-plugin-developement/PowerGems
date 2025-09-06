package dev.iseal.powergems.listeners;

import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FallingBlockHitListener implements Listener {

    List<String> entityList = new ArrayList<>();

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock && containsBlock(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            this.removeEntityBlock(event.getEntity().getUniqueId());
        }
    }

    public void addEntityUUID(UUID id) {
        String uuid = id.toString();
        this.entityList.add(uuid);
    }

    public void removeEntityBlock(UUID id) {
        String uuid = id.toString();
        this.entityList.remove(uuid);
    }

    public boolean containsBlock(UUID id) {
        String uuid = id.toString();
        return this.entityList.contains(uuid);
    }
}
