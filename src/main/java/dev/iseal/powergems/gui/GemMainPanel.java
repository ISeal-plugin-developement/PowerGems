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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.Configuration.GemLoreConfigManager;
import dev.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class GemMainPanel implements Listener {

    protected final Logger logger = PowerGems.getPlugin().getLogger();
    protected final GemManager gemManager;
    protected final CooldownManager cooldownManager;
    protected final GemMaterialConfigManager gemMaterialConfigManager;
    protected final GemLoreConfigManager gemLoreConfigManager;
    protected final Inventory panelInventory;
    protected final HashMap<Integer, String> slotToGemMap = new HashMap<>();
    protected static final int PADDING = 9; // One row padding
    protected final SingletonManager singletonManager;
    private final GemReflectionManager grm = new GemReflectionManager();

    protected GemManager getGemManager() {
        return SingletonManager.getInstance().gemManager;
    }

    /**
     * Constructs a new GemMainPanel with necessary manager instances.
     */
    public GemMainPanel() {
        this.singletonManager = SingletonManager.getInstance();
        this.gemManager = singletonManager.gemManager;
        this.cooldownManager = singletonManager.cooldownManager;
        this.gemMaterialConfigManager = singletonManager.configManager.getRegisteredConfigInstance(GemMaterialConfigManager.class);
        this.gemLoreConfigManager = singletonManager.configManager.getRegisteredConfigInstance(GemLoreConfigManager.class);
        
        int size = calculateInventorySize(gemManager.getAllGems().size());
        this.panelInventory = Bukkit.createInventory(null, size, I18N.translate("GUI_PANEL_TITLE")); 
        setupPanel();
    }

    /**
     * Creates a display ItemStack for a gem in the cooldown panel.
     *
     * @param gemName The name of the gem to create a display for
     * @return An ItemStack representing the gem in the panel, or null if invalid
     */
    protected ItemStack createGemDisplay(String gemName) {
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
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

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

    @EventHandler
    public Gem onGemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isGemCooldownManager(player.getOpenInventory().getTopInventory())) {
            return null; 
        }

        ItemStack item = event.getItem();
        return getInteractedGem(item, player);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Cancel any drag actions in our panel inventory.
        if (isGemCooldownManager(event.getView().getTopInventory())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (isGemCooldownManager(event.getView().getTopInventory())) {
            Player player = (Player) event.getPlayer();
            player.playSound(player.getLocation(), Sound.UI_TOAST_OUT, 0.5f, 1.0f);
        }
    }

    /**
     * Prevents items from being moved out of the panel and triggers an action
     * when a gem is clicked.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(panelInventory)) {
            event.setCancelled(true);

            if (event.getClickedInventory() == null || !event.getClickedInventory().equals(panelInventory)) {
                return;
            }

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
                GemCooldownPanel cooldownPanel = new GemCooldownPanel();
                cooldownPanel.updateInteractedGem(clickedItem, player);
                cooldownPanel.setupGemCooldownPanel();
                player.openInventory(cooldownPanel.panelInventory);
            }
        }
    }

    /**
     * Sets up the panel inventory with gems and their cooldowns.
     */
    protected void setupPanel() {
        panelInventory.clear();
        slotToGemMap.clear();
        final AtomicInteger slotIndex = new AtomicInteger(PADDING);

        setHeaderItem(4);

        // Iterate over all registered gems.
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
     *
     * @param slot The inventory slot to place the header item in
     */
    protected void setHeaderItem(int slot) {
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
     * Fills empty slots in the inventory with spacers.
     */
    protected void fillEmptySlots() {
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
    protected int calculateInventorySize(int gemCount) {
        return Math.max(((gemCount / 7) + 3) * 9, 27);
    }

    /**
     * Checks whether the given inventory is the Gem Cooldown Panel.
     *
     * @param inv The inventory to check.
     * @return True if it matches, false otherwise.
     */
    protected boolean isGemCooldownManager(Inventory inv) {
        return inv == panelInventory;
    }

    /**
     * Retrieves the interacted gem from the item and player.
     *
     * @param item The item interacted with.
     * @param player The player interacting with the item.
     * @return The gem instance, or null if not a valid gem.
     */
    protected Gem getInteractedGem(ItemStack item, Player player) {
        if (item == null || !gemManager.isGem(item)) {
            return null;
        }
        if (!item.hasItemMeta() || !item.getItemMeta().hasCustomModelData()) {
            return null;
        }
        Class<? extends Gem> gemClass = grm.getGemClass(item, player);
        if (gemClass == null) {
            return null;
        }
        try {
            return gemClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.severe("Error instantiating gem for item " + item + ": " + e.getMessage());
            return null;
        }
    }
}