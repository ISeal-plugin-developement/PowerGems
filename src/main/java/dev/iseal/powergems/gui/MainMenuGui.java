package dev.iseal.powergems.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.metadata.FixedMetadataValue;

public class MainMenuGui {

    final int slots = 27;
    public static final HashMap<UUID, MainMenuGui> createInventory = new HashMap<>();
    final Player player;
    Inventory inventory;

    public MainMenuGui(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, slots, "Power Gems Menu");
        createInventory.put(player.getUniqueId(), this);
        player.setMetadata("OpenedMenu", new FixedMetadataValue(PowerGems.getPlugin(), true));
    }

    public void openGemMenu() {
        items();
        // Fill any empty slots with black glass panes
        for(int i = 0; i < slots; i++) {
            if(inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(inventory);
    }

    private void items() {
        SingletonManager sm = SingletonManager.getInstance();
        GemManager gemManager = sm.gemManager;

        List<ItemStack> validGems = new ArrayList<>();
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            ItemStack gemItem = gemManager.createGem(i, 1);
            if (gemItem != null) {
                validGems.add(gemItem);
            }
        }
        int gemCount = validGems.size();
        if (gemCount == 0) return;
        int rowsNeeded = (int) Math.ceil(gemCount / 9.0);
        int rowsToSkip = Math.max(0, (3 - rowsNeeded) / 2);
        int startRow = rowsToSkip * 9;

        for (int row = 0; row < rowsNeeded; row++) {
            int itemsInThisRow = Math.min(9, gemCount - (row * 9));
            int horizontalPadding = (9 - itemsInThisRow) / 2;

            for (int i = 0; i < itemsInThisRow; i++) {
                int index = row * 9 + i;
                int slot = startRow + (row * 9) + horizontalPadding + i;
                inventory.setItem(slot, validGems.get(index));
            }
        }
    }

    public void clickedGem(int slot, ItemStack gemItem) {
        if (gemItem == null || !SingletonManager.getInstance().gemManager.isGem(gemItem)) {
            return;
        }
        String gemType = SingletonManager.getInstance().gemManager.getName(gemItem);
        int gemLevel = SingletonManager.getInstance().gemManager.getLevel(gemItem);

        player.setMetadata("SelectedGem", new FixedMetadataValue(PowerGems.getPlugin(), gemType));
        player.setMetadata("SelectedGemSlot", new FixedMetadataValue(PowerGems.getPlugin(), slot));
        player.closeInventory();
    }
}