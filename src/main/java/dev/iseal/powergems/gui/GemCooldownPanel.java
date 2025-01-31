package dev.iseal.powergems.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.Configuration.GemLoreConfigManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;

/**
 * Manages the inventory GUI that displays gem cooldowns. Prevents players from
 * taking items from the GUI except for interacting with a refresh button and an
 * anvil for editing cooldowns.
 */
public class GemCooldownPanel implements Listener {

    private final SingletonManager sm;
    private final GemManager gemManager;
    private final CooldownManager cooldownManager;
    private final Inventory panelInventory;

    public GemCooldownPanel() {
        this.sm = SingletonManager.getInstance();
        this.gemManager = sm.gemManager;
        this.cooldownManager = sm.cooldownManager;
        int size = calculateInventorySize(gemManager.getGems().size());
        this.panelInventory = Bukkit.createInventory(null, size, ChatColor.DARK_AQUA + "Gem Cooldown Manager:");
        addRefreshButton();
    }

    /**
     * Opens the GUI for a player.
     */
    public void open(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            setupPanel(player);
            player.openInventory(panelInventory);
        }
    }

    /**
     * Refresh/Anvil GUI click handling.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isGemCooldownManager(event.getView().getTitle())) {
            return;
        }
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getRawSlot();
        int refreshButtonSlot = panelInventory.getSize() - 1;

        // Open an anvil GUI when right-clicking the refresh button
        if (event.getClick() == ClickType.RIGHT && clickedSlot == refreshButtonSlot) {
            openCooldownConfigAnvil(player);
            return;
        }

        // Simple refresh on left-click
        if (clickedSlot == refreshButtonSlot && event.getClick() == ClickType.LEFT) {
            setupPanel(player);
            player.sendMessage(ChatColor.GREEN + "Gem Cooldown Manager refreshed!");
        }
    }

    /**
     * Cancels drag events to prevent item manipulation in the panel.
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isGemCooldownManager(event.getView().getTitle())) {
            event.setCancelled(true);
        }
    }

    /**
     * Opens the anvil interface to let players set a new cooldown.
     */
    private void openCooldownConfigAnvil(Player player) {
        Inventory anvilInv = Bukkit.createInventory(null, InventoryType.ANVIL, ChatColor.DARK_GREEN + "Update Cooldown");

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + "Enter new cooldown in seconds");
            paper.setItemMeta(meta);
        }
        anvilInv.setItem(0, paper);

        player.openInventory(anvilInv);
    }

    /**
     * Processes clicks in the anvil to update config.
     */
    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ChatColor.DARK_GREEN + "Update Cooldown")) {
            return;
        }
        event.setCancelled(true);

        // If the user clicks the anvil result slot
        if (event.getRawSlot() == 2 && event.getCurrentItem() != null) {
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            try {
                long newCooldown = Long.parseLong(ChatColor.stripColor(displayName));

                // Update config for leftClick, or your chosen path
                sm.configManager.set("cooldown.leftClick", newCooldown);
                sm.configManager.saveConfig();

                // Reload cooldowns to apply immediately
                cooldownManager.reloadFromConfig();

                Player p = (Player) event.getWhoClicked();
                p.sendMessage(ChatColor.GREEN + "Updated cooldown to " + newCooldown + "s!");
                p.closeInventory();
                open(p);
            } catch (NumberFormatException e) {
                ((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Invalid number input!");
            }
        }
    }

    /**
     * Builds lore for gem items, including cooldown info and listed powers.
     */
    private List<String> buildCooldownLore(Player player, String gemName) {
        List<String> lore = new ArrayList<>();
        Gem gem = gemManager.getGems().get(gemName);
        if (gem == null) {
            Bukkit.getLogger().warning("[PowerGems] Gem not found: " + gemName);
            lore.add(ChatColor.RED + "Error: Gem data not found.");
            return lore;
        }

        lore.add(""); // spacing
        lore.add(ChatColor.YELLOW + "Cooldowns:");

        String[] actions = { "left", "right", "shift" };
        String[] actionDisplayNames = { "Left Click", "Right Click", "Shift Click" };

        // Example: getFullCooldown(level, gemName, action)
        for (int i = 0; i < actions.length; i++) {
            long defaultCooldown = cooldownManager.getFullCooldown(gem.getLevel(), gemName, actions[i]);
            lore.add(ChatColor.GREEN + actionDisplayNames[i] + ": " + defaultCooldown + "s");
        }

        lore.add("");
        lore.add(ChatColor.GREEN + "Powers:");
        GemLoreConfigManager loreConfig = sm.configManager.getRegisteredConfigInstance(GemLoreConfigManager.class);
        List<String> powers = loreConfig.getLore(GemManager.lookUpID(gemName));

        if (powers != null && !powers.isEmpty()) {
            for (String power : powers) {
                lore.add(ChatColor.WHITE + "- " + power);
            }
        } else {
            lore.add(ChatColor.RED + "No powers defined.");
        }

        return lore;
    }

    /**
     * Sets up the GUI with gem items plus a refresh button.
     */
    private void setupPanel(Player player) {
        panelInventory.clear();
        int slotIndex = 0;

        for (Gem gem : gemManager.getGems().values()) {
            if (slotIndex >= panelInventory.getSize() - 1) {
                break;
            }

            String gemName = gem.getName();
            Material mat = gemManager.getGemMaterial(gemName);
            if (mat == null) {
                Bukkit.getLogger().warning("[PowerGems] Material for gem '" + gemName + "' is not defined. Skipping.");
                continue;
            }

            // Example method: generateItemStack(String gemName, int gemLevel)
            ItemStack gemItem = gemManager.generateItemStack(gemName, gem.getLevel());
            gemItem = gem.gemInfo(gemItem);

            ItemMeta meta = gemItem.getItemMeta();
            if (meta != null) {
                meta.setLore(buildCooldownLore(player, gemName));
                gemItem.setItemMeta(meta);
            }

            panelInventory.setItem(slotIndex++, gemItem);
        }
        fillEmptySlots(slotIndex);
    }

    /**
     * Fills empty slots with placeholder items.
     */
    private void fillEmptySlots(int startIndex) {
        ItemStack placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = placeholder.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            placeholder.setItemMeta(meta);
        }

        for (int i = startIndex; i < panelInventory.getSize() - 1; i++) {
            panelInventory.setItem(i, placeholder);
        }
    }

    /**
     * Makes sure the inventory has enough rows to hold gems plus one slot for refresh.
     */
    private int calculateInventorySize(int gemCount) {
        return Math.max(((gemCount / 9) + 1) * 9, 27);
    }

    /**
     * Adds a refresh button in the last slot.
     */
    private void addRefreshButton() {
        ItemStack refreshButton = new ItemStack(Material.COMPASS);
        ItemMeta meta = refreshButton.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Refresh Cooldowns");
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Right-click to edit cooldown; left-click to refresh"));
            refreshButton.setItemMeta(meta);
        }
        panelInventory.setItem(panelInventory.getSize() - 1, refreshButton);
    }

    /**
     * Checks if the title matches our Gem Cooldown Manager.
     */
    private boolean isGemCooldownManager(String title) {
        return Objects.equals(title, ChatColor.DARK_AQUA + "Gem Cooldown Manager:");
    }
}

