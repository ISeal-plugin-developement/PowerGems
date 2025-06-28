package dev.iseal.powergems.listeners.powerListeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class SandMoveListener implements Listener {

    private final HashMap<UUID, Block> slowSandList = new HashMap<>();
    private final HashMap<UUID, HashMap<Block, Material>> toReplace = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!slowSandList.isEmpty()) {
            Player plr = e.getPlayer();
            Location to = e.getTo();
            for (UUID id : slowSandList.keySet()) {
                Block block = slowSandList.get(id);
                if (!block.getWorld().equals(to.getWorld())) {
                    continue;
                }
                if (id.equals(plr.getUniqueId())) {
                    continue;
                }
                if (to.distance(block.getRelative(BlockFace.UP).getLocation()) < 0.9) {
                    plr.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2));
                }
            }
        }
    }

    public void addToList(Block block, UUID id) {
        slowSandList.put(id, block);
    }

    public void addToRemoveList(UUID id, HashMap<Block, Material> blocks) {
        toReplace.put(id, blocks);
    }

    public void removeFromList(UUID id) {
        toReplace.get(id).forEach(Block::setType);
        toReplace.remove(id);
        slowSandList.remove(id);
    }

    public boolean hasBlock(Block block) {
        return slowSandList.containsValue(block);
    }

    public boolean hasToRemoveFrom(UUID uniqueId) {
        return toReplace.containsKey(uniqueId);
    }
}
