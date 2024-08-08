package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class DeathEvent implements Listener {

    private Map<UUID, List<ItemStack>> keepItems = new HashMap<>();
    private final GeneralConfigManager generalConfigManager = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!generalConfigManager.doKeepGemsOnDeath())
            return;
        final List<ItemStack> toKeep = new ArrayList<>();

        for (ItemStack item : e.getDrops()) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                if (dataContainer.has(nkm.getKey("is_power_gem"), PersistentDataType.BOOLEAN)) {
                    toKeep.add(item);
                }
            }
        }
        if (!toKeep.isEmpty()) {
            e.getDrops().removeAll(toKeep);
            keepItems.put(e.getEntity().getUniqueId(), toKeep);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        final List<ItemStack> toRestore = keepItems.get(e.getPlayer().getUniqueId());
        if (toRestore != null) {
            if (generalConfigManager.doGemDecay()) {
                for (ItemStack item : toRestore) {
                    PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
                    String power = pdc.get(nkm.getKey("gem_power"), PersistentDataType.STRING);
                    if (pdc.get(nkm.getKey("gem_level"), PersistentDataType.INTEGER) > 1) {
                        e.getPlayer().getInventory().addItem(SingletonManager.getInstance().gemManager.createGem(power,
                                pdc.get(nkm.getKey("gem_level"), PersistentDataType.INTEGER) - 1));
                    } else if (!generalConfigManager.doGemDecayOnLevelOne()) {
                        e.getPlayer().getInventory().addItem(SingletonManager.getInstance().gemManager.createGem(power, 1));
                    }
                }
            } else {
                e.getPlayer().getInventory().addItem(toRestore.toArray(new ItemStack[0]));
            }
            keepItems.remove(e.getPlayer().getUniqueId());
        }
    }

}
