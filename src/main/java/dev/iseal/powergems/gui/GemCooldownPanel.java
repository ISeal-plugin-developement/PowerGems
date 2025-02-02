package dev.iseal.powergems.gui;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class GemCooldownPanel extends GemMainPanel {

    private Gem interactedGem;
    private final HashMap<UUID, String> cooldownActionMap = new HashMap<>();

    public GemCooldownPanel() {
        super();
    }

    public void setupGemCooldownPanel() {
        setupPanel();
        fillEmptySlots();
        setTheItemsToEditCooldowns(4);
        setBackButton(panelInventory.getSize() - 1);
    }

    public void updateInteractedGem(ItemStack item, Player player) {
        this.interactedGem = getInteractedGem(item, player);
    }

    public Gem getInteractedGemInstance() {
        return interactedGem;
    }

    @Override
    protected void setHeaderItem(int slot) {
        if (interactedGem != null) {
            String gemName = interactedGem.getName();
            if (gemName != null) {
                ItemStack gemDisplay = createGemDisplay(gemName);
                if (gemDisplay != null) {
                    panelInventory.setItem(slot, gemDisplay);
                    return;
                }
            }
        }
        super.setHeaderItem(slot);
    }

    public void setTheItemsToEditCooldowns(int slot) {
        super.setHeaderItem(slot);
        panelInventory.setItem(
            slot + 1, 
            createButton(
                Material.LIME_STAINED_GLASS_PANE, 
                I18N.translate("LEFT_CLICK_COOLDOWN"), 
                ChatColor.GOLD
            )
        );
        panelInventory.setItem(
            slot + 2,  
            createButton(
                Material.RED_STAINED_GLASS_PANE, 
                I18N.translate("RIGHT_CLICK_COOLDOWN"), 
                ChatColor.GOLD
            )
        );
        panelInventory.setItem(
            slot + 3, 
            createButton(
                Material.BLACK_STAINED_GLASS_PANE, 
                I18N.translate("SHIFT_CLICK_COOLDOWN"), 
                ChatColor.GOLD
            )
        );
    }

    private void setBackButton(int slot) {
        panelInventory.setItem(
            slot, 
            createButton(
                Material.ARROW, 
                I18N.translate("BACK_TO_MAIN_MENU"), 
                ChatColor.RED
            )
        );
    }

    /**
     * Helper method to create an ItemStack with a custom display name.
     */
    private ItemStack createButton(Material material, String displayName, ChatColor color) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color + displayName);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onCooldownItemClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().equals(panelInventory)) {
            event.setCancelled(true);

            if (event.getClickedInventory() == null || !event.getClickedInventory().equals(panelInventory)) {
                return;
            }

            if (!(event.getWhoClicked() instanceof Player player)) {
                return;
            }

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) {
                return;
            }

            String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

            if (displayName.equals(I18N.translate("LEFT_CLICK_COOLDOWN"))) {
                openAnvil(player, "left");
            } else if (displayName.equals(I18N.translate("RIGHT_CLICK_COOLDOWN"))) {
                openAnvil(player, "right");
            } else if (displayName.equals(I18N.translate("SHIFT_CLICK_COOLDOWN"))) {
                openAnvil(player, "shift");
            } else if (displayName.equals(I18N.translate("BACK_TO_MAIN_MENU"))) {
                player.closeInventory();
                open((CommandSender) player);
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().equals(panelInventory)) {
            event.setCancelled(true);
        }
    }

    /**
     * Opens an anvil GUI for the player to enter a numeric value.
     * @param player The player to open the anvil for.
     * @param action The cooldown action ("left", "right", or "shift").
     */
    public void openAnvil(Player player, String action) {
        String anvilTitle = ChatColor.AQUA + I18N.translate("SET_COOLDOWN") + " " + action;
        Inventory anvilInv = Bukkit.createInventory(null, InventoryType.ANVIL, anvilTitle);
        ItemStack paper = createButton(Material.PAPER, I18N.translate("ENTER_NUMBER"), ChatColor.WHITE);
        anvilInv.setItem(0, paper);
        cooldownActionMap.put(player.getUniqueId(), action);
        player.openInventory(anvilInv);
    }
    
    /**
     * Handles input from the anvil GUI.
     */
    @EventHandler
    public void onAnvilInput(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title == null || !title.contains(I18N.translate("SET_COOLDOWN_PREFIX")) || !title.contains(I18N.translate("COOLDOWN_SUFFIX"))) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (event.getRawSlot() != 2) {
            return;
        }
        ItemStack result = event.getCurrentItem();
        if (result == null || !result.hasItemMeta()) {
            return;
        }
        String input = ChatColor.stripColor(result.getItemMeta().getDisplayName());
        try {
            int newCooldown = Integer.parseInt(input);
            String action = cooldownActionMap.get(player.getUniqueId());
            if (interactedGem != null) {
                String gemName = interactedGem.getName();
                // TODO: Implement cooldown updating in CooldownManager
                // singletonManager.cooldownManager.updateCooldown(gemName, action, newCooldown);
                logger.warning("Cooldown update not yet implemented for " + gemName + " " + action + ": " + newCooldown);
                player.sendMessage(ChatColor.GREEN + I18N.translate("COOLDOWN_SET") + " " + action + " " + newCooldown + "s");
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + I18N.translate("INVALID_NUMBER") + ": " + input);
            logger.warning("Invalid number input in anvil GUI: " + input);
        }
        event.setCancelled(true);
        player.closeInventory();
        cooldownActionMap.remove(player.getUniqueId());
        setupGemCooldownPanel();
        player.openInventory(panelInventory);
    }

    public Inventory getInventory() {
        return panelInventory;
    }
}