package dev.iseal.powergems.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;

public class GemCooldownPanel {

    private final GemManager gemManager;
    private final CooldownManager cooldownManager;
    private final Inventory panelInventory;

    public GemCooldownPanel(GemManager gemManager, CooldownManager cooldownManager) {
        this.gemManager = gemManager;
        this.cooldownManager = cooldownManager;
        // Dynamically set inventory size based on number of gems (multiple of 9)
        int size = ((gemManager.getGems().size() / 9) + 1) * 9;
        this.panelInventory = Bukkit.createInventory(null, size, ChatColor.DARK_AQUA + "Gem Cooldowns");
    }

    /**
     * Opens the cooldown panel for the sender if they are a player.
     * 
     * @param sender The CommandSender requesting to open the panel.
     */
    public void open(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            setupPanel(player);
            player.openInventory(panelInventory);
        }
    }

    /**
     * Populates the panel inventory with gems and their cooldown information.
     * 
     * @param player The player for whom the cooldowns are displayed.
     */
    private void setupPanel(Player player) {
        panelInventory.clear();
        int slotIndex = 0;

        for (dev.iseal.powergems.misc.AbstractClasses.Gem gem : gemManager.getGems().values()) {
            if (slotIndex >= panelInventory.getSize()) {
                break; // No more slots available
            }

            // Create a basic ItemStack for the gem
            ItemStack gemItem = new ItemStack(org.bukkit.Material.EMERALD); // Default material, will be overridden by gemInfo
            
            // Get gem information using the implemented gemInfo method
            gemItem = gem.gemInfo(gemItem);
            ItemMeta meta = gemItem.getItemMeta();
            
            if (meta != null) {
                // Get existing lore from gemInfo
                java.util.ArrayList<String> lore = new java.util.ArrayList<>(meta.getLore());
                
                // Add cooldown information
                lore.add("");
                lore.add(ChatColor.YELLOW + "Cooldowns:");
                lore.add(ChatColor.WHITE + "Left Click: " + cooldownManager.getFormattedTimer(player, gem.getClass(), "left"));
                lore.add(ChatColor.WHITE + "Right Click: " + cooldownManager.getFormattedTimer(player, gem.getClass(), "right"));
                lore.add(ChatColor.WHITE + "Shift Click: " + cooldownManager.getFormattedTimer(player, gem.getClass(), "shift"));
                
                meta.setLore(lore);
                gemItem.setItemMeta(meta);
                panelInventory.setItem(slotIndex++, gemItem);
            }
        }

        // Optionally, fill remaining slots with placeholder items for aesthetic purposes
        ItemStack placeholder = new ItemStack(org.bukkit.Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        if (placeholderMeta != null) {
            placeholderMeta.setDisplayName(" ");
            placeholder.setItemMeta(placeholderMeta);
        }

        for (; slotIndex < panelInventory.getSize(); slotIndex++) {
            panelInventory.setItem(slotIndex, placeholder);
        }
    }
}

