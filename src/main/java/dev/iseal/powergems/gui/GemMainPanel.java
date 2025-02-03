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
import org.bukkit.event.inventory.ClickType;
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
    protected static final int PADDING = 9;
    protected final SingletonManager singletonManager;
    private final GemReflectionManager grm = new GemReflectionManager();

    public GemMainPanel() {
        this.singletonManager = SingletonManager.getInstance();
        this.gemManager = singletonManager.gemManager;
        this.cooldownManager = singletonManager.cooldownManager;
        this.gemMaterialConfigManager = singletonManager.configManager
            .getRegisteredConfigInstance(GemMaterialConfigManager.class);
        this.gemLoreConfigManager = singletonManager.configManager
            .getRegisteredConfigInstance(GemLoreConfigManager.class);
        int size = calculateInventorySize(gemManager.getAllGems().size());
        this.panelInventory = Bukkit.createInventory(null, size, I18N.translate("GUI_PANEL_TITLE"));
        setupPanel();
    }

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
        return getInteractedGem(event.getItem(), player);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().equals(panelInventory)) {
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInv = event.getView().getTopInventory();
        Inventory clickedInv = event.getClickedInventory();
        
        if (!topInv.equals(panelInventory)) {
            return;
        }
        

        if (clickedInv != null && clickedInv.equals(panelInventory)) {
            event.setCancelled(true);
        }
        
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        

        if (clickedInv == null || !clickedInv.equals(panelInventory)) {
            return;
        }
        
      
        if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) {
            event.setCancelled(true);
            return;
        }
        
       
        if (event.getRawSlot() >= panelInventory.getSize()) {
            return;
        }
        
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            return;
        }
        
        String gemName = slotToGemMap.get(event.getRawSlot());
        if (gemName != null) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            GemCooldownPanel cooldownPanel = new GemCooldownPanel();
            cooldownPanel.updateInteractedGem(currentItem, player);
            if (cooldownPanel.getInteractedGemInstance() == null) {
                logger.warning("Gem detection failed for item in slot " + event.getRawSlot() + " (gem name: " + gemName + "). Check configuration and custom model data.");
                return;
            }
            cooldownPanel.setupGemCooldownPanel();
            player.openInventory(cooldownPanel.getInventory());
        }
    }

    protected void setupPanel() {
        panelInventory.clear();
        slotToGemMap.clear();
        AtomicInteger slotIndex = new AtomicInteger(PADDING);
        setHeaderItem(4);
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

    protected void setHeaderItem(int slot) {
        ItemStack header = new ItemStack(Material.END_CRYSTAL);
        ItemMeta meta = header.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "✧ " + ChatColor.AQUA + I18N.translate("HEADER_TITLE")
                    + ChatColor.GOLD + " ✧");
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

    protected int calculateInventorySize(int gemCount) {
        return Math.max(((gemCount / 7) + 3) * 9, 27);
    }

    protected boolean isGemCooldownManager(Inventory inv) {
        return inv == panelInventory;
    }

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

    public Inventory getInventory() {
        return panelInventory;
    }
}