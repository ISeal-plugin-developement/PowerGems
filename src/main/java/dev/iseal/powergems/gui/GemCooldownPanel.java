package dev.iseal.powergems.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

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

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.Configuration.GemLoreConfigManager;
import dev.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class GemCooldownPanel implements Listener {

    private final Logger logger = PowerGems.getPlugin().getLogger();
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
        
        int size = calculateInventorySize(gemManager.getAllGems().size());
        this.panelInventory = Bukkit.createInventory(null, size, 
            I18N.translate("GUI_COOLDOWN_PANEL_TITLE")); 
            
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
            logger.warning(I18N.translate("LOGGER_MATERIAL_NOT_DEFINED") + ": " + gemName);
            return null;
        }

        ItemStack display = new ItemStack(gemMaterial);
        ItemMeta meta = display.getItemMeta();
        if (meta == null) return null;

        int gemID = GemManager.lookUpID(gemName); 
        if (gemID != 0) { 
            meta.setCustomModelData(gemID);
        } else {
            logger.warning(I18N.translate("LOGGER_NO_CUSTOM_MODEL_DATA") + ": " + gemName);
        }

        meta.setDisplayName(ChatColor.GOLD + "✧ " + ChatColor.AQUA + gemName + " Gem" + ChatColor.GOLD + " ✧");
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.YELLOW + I18N.translate("LORE_COOLDOWNS"));

        String[] actions = {"left", "right", "shift"};
        String[] actionIcons = {"➔", "⬆", "⇧"};

        String standardizedGemName = GemManager.lookUpName(gemID);

        for (int i = 0; i < actions.length; i++) {
            long cooldownTime = cooldownManager.getFullCooldown(1, standardizedGemName, actions[i]);
            String translatedActionCooldown = I18N.translate("ACTION_COOLDOWN") + ": " + cooldownTime + "s";
            lore.add(ChatColor.GREEN + actionIcons[i] + " " + translatedActionCooldown);
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
        if (!isGemCooldownManager(event.getView().getTopInventory())) {
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
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.AQUA + I18N.translate("GEM_INFO_TITLE") + 
                                ChatColor.GOLD + " ===");
            
            String[] actions = {"left", "right", "shift"};
            String[] actionIcons = {"➔", "⬆", "⇧"};
            for (int i = 0; i < actions.length; i++) {
                long cooldownTime = cooldownManager.getFullCooldown(1, gemName, actions[i]);
                String translatedCooldownInfo = I18N.translate("ACTION_COOLDOWN_INFO") + ": " + cooldownTime + "s";
                player.sendMessage(ChatColor.GREEN + actionIcons[i] + " " + translatedCooldownInfo);
            }

            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + I18N.translate("POWERS_TITLE"));
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
        if (isGemCooldownManager(event.getView().getTopInventory())) {
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
        if (isGemCooldownManager(event.getView().getTopInventory())) {
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
        final AtomicInteger slotIndex = new AtomicInteger(PADDING);

        setHeaderItem(4);

        // Iterate over all registered gems using existing project methods.
        gemManager.getAllGems().forEach((id, gem) -> {
            String gemName = gemManager.getGemName(gem);
            ItemStack gemItem = createGemDisplay(gemName);
            if (gemItem != null) {
                int currentSlot = slotIndex.getAndIncrement();
                panelInventory.setItem(currentSlot, gemItem);
                slotToGemMap.put(currentSlot, gemName);
            }
        });

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
            meta.setDisplayName(ChatColor.GOLD + "✧ " + ChatColor.AQUA + I18N.translate("HEADER_TITLE") + ChatColor.GOLD + " ✧");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + I18N.translate("HEADER_LORE_1"));
            lore.add(ChatColor.GRAY + I18N.translate("HEADER_LORE_2"));
            lore.add("");
            lore.add(ChatColor.YELLOW + I18N.translate("HEADER_LORE_3"));
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
     * Checks whether the given inventory is the Gem Cooldown Panel.
     *
     * @param inv The inventory to check.
     * @return True if it matches, false otherwise.
     */
    private boolean isGemCooldownManager(Inventory inv) {
        return inv == panelInventory;
    }
}

