package me.iseal.powergems.listeners;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.Configuration.GeneralConfigManager;
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
    private final GeneralConfigManager generalConfigManager = Main.getSingletonManager().configManager.getGeneralConfigManager();

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!Main.config.getBoolean("keepGemsOnDeath"))
            return;
        final List<ItemStack> toKeep = new ArrayList<>();

        for (ItemStack item : e.getDrops()) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
                if (dataContainer.has(Main.getIsGemKey(), PersistentDataType.BOOLEAN)) {
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
                    String power = pdc.get(Main.getGemPowerKey(), PersistentDataType.STRING);
                    if (pdc.get(Main.getGemLevelKey(), PersistentDataType.INTEGER) > 1) {
                        e.getPlayer().getInventory().addItem(Main.getSingletonManager().gemManager.createGem(power,
                                pdc.get(Main.getGemLevelKey(), PersistentDataType.INTEGER) - 1));
                    } else if (!Main.config.getBoolean("doDecayOnLevel1")) {
                        e.getPlayer().getInventory().addItem(Main.getSingletonManager().gemManager.createGem(power, 1));
                    }
                }
            } else {
                e.getPlayer().getInventory().addItem(toRestore.toArray(new ItemStack[0]));
            }
            keepItems.remove(e.getPlayer().getUniqueId());
        }
    }

}
