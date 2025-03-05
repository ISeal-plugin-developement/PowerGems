package dev.iseal.powergems.gui;

import dev.iseal.powergems.PowerGems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class GemConfiguratorGui {

    public static final HashMap<UUID, GemConfiguratorGui> openInventories = new HashMap<>();
    private final Player player;
    private final Inventory inventory;
    private final ItemStack selectedGem;

    public GemConfiguratorGui(Player player, ItemStack clickedGem) {
        this.player = player;
        this.selectedGem = clickedGem;
        int slots = 36;
        this.inventory = Bukkit.createInventory(player, slots, "Gem Configurator");
        openInventories.put(player.getUniqueId(), this);
        player.setMetadata("OpenedConfigurator", new FixedMetadataValue(PowerGems.getPlugin(), true));
    }

    public void openConfigurator() {
        setupModifierItems();

        // Fill empty slots with black stained-glass panes
        for(int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(inventory);
    }

    private void setupModifierItems() {
        if (selectedGem != null) {
            inventory.setItem(13, selectedGem);
        }

        // Add back button
        ItemStack backButton = createBackButton(Material.BARRIER, "§cBack to Main Menu");
        inventory.setItem(31, backButton);
    }

    private ItemStack createBackButton(Material material, String name, String... lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            button.setItemMeta(meta);
        }
        return button;
    }

    public void handleClick(int slot) {
        if (slot == 31) {
            player.removeMetadata("OpenedConfigurator", PowerGems.getPlugin());
            MainMenuGui mainMenu = new MainMenuGui(player);
            mainMenu.openGemMenu();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}