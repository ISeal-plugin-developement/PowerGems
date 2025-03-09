package dev.iseal.powergems.listeners;

import dev.iseal.powergems.PowerGems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.gui.GemConfiguratorGui;

public class GuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(player.hasMetadata("OpenedMenu")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && SingletonManager.getInstance().gemManager.isGem(clickedItem)) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();

                    if (pdc.has(SingletonManager.getInstance().namespacedKeyManager.getKey("gem_power"), PersistentDataType.STRING)) {
                        String gemType = pdc.get(SingletonManager.getInstance().namespacedKeyManager.getKey("gem_power"), PersistentDataType.STRING);

                        // Store selected gem info
                        player.setMetadata("SelectedGem", new FixedMetadataValue(PowerGems.getPlugin(), gemType));
                        player.setMetadata("SelectedGemSlot", new FixedMetadataValue(PowerGems.getPlugin(), event.getSlot()));

                        player.closeInventory();

                        GemConfiguratorGui configurator = new GemConfiguratorGui(player, clickedItem);
                        configurator.openConfigurator();

                        player.setMetadata("OpenedConfigurator", new FixedMetadataValue(PowerGems.getPlugin(), true));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.hasMetadata("OpenedMenu")) {
            player.removeMetadata("OpenedMenu", PowerGems.getPlugin());
        }
        if (player.hasMetadata("OpenedConfigurator")) {
            if (event.getView().getTitle().contains("Gem Configurator")) {
                player.removeMetadata("OpenedConfigurator", PowerGems.getPlugin());
                if (player.hasMetadata("SelectedGem")) {
                    player.removeMetadata("SelectedGem", PowerGems.getPlugin());
                }
                if (player.hasMetadata("SelectedGemSlot")) {
                    player.removeMetadata("SelectedGemSlot", PowerGems.getPlugin());
                }
            }
        }
    }
}