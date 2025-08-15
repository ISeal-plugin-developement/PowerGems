package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class KillEventListener implements Listener {
    
    private final GemManager gemManager;
    private final SingletonManager sm;

    public KillEventListener() {
        this.sm = SingletonManager.getInstance();
        this.gemManager = sm.gemManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        int maxLevel = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class).getMaxGemLevel();

        ArrayList<ItemStack> playerGems = gemManager.getPlayerGems(killer);
        
        for (ItemStack gem : playerGems) {
            int currentLevel = gemManager.getLevel(gem);
            if (currentLevel >= maxLevel) continue;

            gemManager.attemptFixGem(gem);

            // Upgrade gem
            ItemMeta meta = gem.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(sm.namespacedKeyManager.getKey("gem_level"), PersistentDataType.INTEGER, currentLevel + 1);
            meta = gemManager.createLore(meta);
            gem.setItemMeta(meta);
        }
    }
}