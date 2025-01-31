package dev.iseal.powergems.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.Configuration.GemLoreConfigManager;
import dev.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem; // Add this import

public class GemCooldownPanel implements Listener {

    private final GemManager gemManager;
    private final CooldownManager cooldownManager;
    private final GemMaterialConfigManager gemMaterialConfigManager;
    private final GemLoreConfigManager gemLoreConfigManager;
    private final Inventory panelInventory;
    private final HashMap<Integer, String> slotToGemMap = new HashMap<>();
    private static final int PADDING = 9; // One row padding
    private final SingletonManager singletonManager;

    /**
     * Constructs a new GemCooldownPanel with necessary manager instances.
     */
    public GemCooldownPanel() {
        this.singletonManager = SingletonManager.getInstance();
        this.gemManager = singletonManager.gemManager;
        this.cooldownManager = singletonManager.cooldownManager;
        this.gemMaterialConfigManager = singletonManager.configManager.getRegisteredConfigInstance(GemMaterialConfigManager.class);
        this.gemLoreConfigManager = singletonManager.configManager.getRegisteredConfigInstance(GemLoreConfigManager.class);
        
        int size = calculateInventorySize(gemManager.getGems().size());
        this.panelInventory = Bukkit.createInventory(null, size, 
            ChatColor.DARK_AQUA + "❖ Gem Cooldown Manager ❖");
            
        setupPanel();
    }

    /**
     * Creates a display ItemStack for a gem in the cooldown panel.
     *
     * @param gemName The name of the gem to create a display for
     * @return An ItemStack representing the gem in the panel, or null if invalid
     */
    private ItemStack createGemDisplay(String gemName) {
        Material gemMaterial = gemMaterialConfigManager.getGemMaterial(gemName);
        if (gemMaterial == null) {
            Bukkit.getLogger().warning("[PowerGems] Material for gem '" + gemName + "' is not defined.");
            return null;
        }

        ItemStack display = new ItemStack(gemMaterial);
        ItemMeta meta = display.getItemMeta();
        if (meta == null) return null;

        // Set custom model data based on gem ID
        int gemID = GemManager.lookUpID(gemName);
        meta.setCustomModelData(gemID); // This matches the custom model data set in GemManager

        // Add name and enchant effect
        meta.setDisplayName(ChatColor.GOLD + "✧ " + ChatColor.AQUA + gemName + " Gem" + ChatColor.GOLD + " ✧");
        meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);

        // Build lore
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + "⌚ Cooldowns:");
        
        String[] actions = {"left", "right", "shift"};
        String[] actionIcons = {"➔", "⬆", "⇧"};
        String[] actionNames = {"Left Click", "Right Click", "Shift Click"};

        // Add cooldowns
        for (int i = 0; i < actions.length; i++) {
            long cooldown = cooldownManager.getFullCooldown(1, gemName, actions[i]);
            lore.add(ChatColor.GREEN + actionIcons[i] + " " + actionNames[i] + ": " + 
                    ChatColor.WHITE + cooldown + "s");
        }

        // Add powers
        lore.add("");
        lore.add(ChatColor.YELLOW + "✦ Powers:");
        List<String> powers = gemLoreConfigManager.getLore(gemID);
        if (powers != null && !powers.isEmpty()) {
            for (String power : powers) {
                lore.add(ChatColor.WHITE + "• " + power);
            }
        } else {
            lore.add(ChatColor.RED + "No powers defined");
        }

        meta.setLore(lore);
        display.setItemMeta(meta);
        return display;
    }

    /**
     * Opens the gem cooldown panel GUI for a player.
     */
    public void open(CommandSender sender) {
        if (sender instanceof Player player) {
            setupPanel();
            player.openInventory(panelInventory);
            player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 0.5f, 1.0f);
        }
    }

    /**
     * Handles inventory click events within the Gem Cooldown Panel.
     *
     * @param event The InventoryClickEvent triggered by the player.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isGemCooldownManager(event.getView().getTitle())) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            return;
        }

        String gemName = slotToGemMap.get(event.getSlot());
        if (gemName != null) {
            // Play sound effect
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            
            // Send detailed gem info message
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.AQUA + gemName + " Gem Info" + 
                                ChatColor.GOLD + " ===");
            
            // Show cooldowns
            String[] actions = {"left", "right", "shift"};
            String[] actionNames = {"Left Click", "Right Click", "Shift Click"};
            for (int i = 0; i < actions.length; i++) {
                long cooldown = cooldownManager.getFullCooldown(1, gemName, actions[i]);
                player.sendMessage(ChatColor.GREEN + actionNames[i] + ": " + 
                                    ChatColor.WHITE + cooldown + "s");
            }

            // Show powers
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Powers:");
            int gemID = GemManager.lookUpID(gemName);
            List<String> powers = gemLoreConfigManager.getLore(gemID);
            if (powers != null) {
                for (String power : powers) {
                    player.sendMessage(ChatColor.WHITE + "• " + power);
                }
            }
            player.sendMessage("");
        }
    }

    /**
     * Cancels drag events to prevent item manipulation in the panel.
     *
     * @param event The InventoryDragEvent triggered by the player.
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isGemCooldownManager(event.getView().getTitle())) {
            event.setCancelled(true);
        }
    }

    /**
     * Plays a sound when the inventory is closed.
     *
     * @param event The InventoryCloseEvent triggered by the player.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (isGemCooldownManager(event.getView().getTitle())) {
            Player player = (Player) event.getPlayer();
            player.playSound(player.getLocation(), Sound.UI_TOAST_OUT, 0.5f, 1.0f);
        }
    }

    /**
     * Sets up the panel inventory with gems and their cooldowns.
     */
    private void setupPanel() {
        panelInventory.clear();
        slotToGemMap.clear();
        int slotIndex = PADDING;

        // Add header item in the middle of first row
        setHeaderItem(4);

        // Add gems with proper spacing
        for (Gem gem : gemManager.getGems().values()) {
            if (slotIndex >= panelInventory.getSize() - PADDING) break;

            String gemName = gem.getName();
            ItemStack gemItem = createGemDisplay(gemName);
            
            if (gemItem != null) {
                panelInventory.setItem(slotIndex, gemItem);
                slotToGemMap.put(slotIndex, gemName);
                slotIndex++;
            }
        }

        fillEmptySlots();
    }

    /**
     * Sets up the header item that explains the panel's purpose.
     * Places an End Crystal with descriptive lore in the specified slot.
     *
     * @param slot The inventory slot to place the header item in
     */
    private void setHeaderItem(int slot) {
        ItemStack header = new ItemStack(Material.END_CRYSTAL);
        ItemMeta meta = header.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "✧ " + ChatColor.AQUA + "Gem Information" + ChatColor.GOLD + " ✧");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "View information about all available gems");
            lore.add(ChatColor.GRAY + "and their cooldown times.");
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click a gem to see detailed information!");
            meta.setLore(lore);
            header.setItemMeta(meta);
        }
        panelInventory.setItem(slot, header);
    }

    /**
     * Fills empty slots in the inventory with spacer items to maintain the layout.
     */
    private void fillEmptySlots() {
        ItemStack spacer = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = spacer.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            spacer.setItemMeta(meta);
        }

        for (int i = 0; i < panelInventory.getSize(); i++) {
            if (panelInventory.getItem(i) == null) {
                panelInventory.setItem(i, spacer);
            }
        }
    }

    /**
     * Ensures the inventory has enough rows to hold gems.
     *
     * @param gemCount The number of gems to display.
     * @return The calculated inventory size.
     */
    private int calculateInventorySize(int gemCount) {
        return Math.max(((gemCount / 7) + 3) * 9, 27);
    }

    /**
     * Checks if the inventory title matches the Gem Cooldown Manager.
     *
     * @param title The title to check.
     * @return True if it matches, false otherwise.
     */
    private boolean isGemCooldownManager(String title) {
        return Objects.equals(title, ChatColor.DARK_AQUA + "❖ Gem Cooldown Manager ❖");
    }
}

