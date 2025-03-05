package dev.iseal.powergems.listeners.passivePowerListeners;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;

public class DamageListener implements Listener {

    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final ArrayList<String> allowedGems = new ArrayList<>(Arrays.asList("Air", "Lightning"));

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player plr))
            return;
        checkIfFall(plr, e);
    }

    private void checkIfFall(Player p, EntityDamageEvent event) {

            if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
                return;
            try {
            for (ItemStack item : gm.getPlayerGems(p)) {
                if (item == null || !item.hasItemMeta())
                    continue;

                ItemMeta meta = item.getItemMeta();
                if (meta == null)
                    continue;

                String gemPower = meta.getPersistentDataContainer()
                        .get(nkm.getKey("gem_power"), PersistentDataType.STRING);
                if (gemPower == null)
                    continue;

                if (allowedGems.contains(gemPower)) {
                    event.setCancelled(true);
                    return;
                }
            }

        } catch (Exception e) {
            event.setCancelled(true);
            e.printStackTrace();
        }
    }
}