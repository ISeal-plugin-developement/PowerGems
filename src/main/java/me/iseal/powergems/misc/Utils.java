package me.iseal.powergems.misc;

import me.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.GemManager;

import java.util.ArrayList;
import java.util.Objects;

public class Utils {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final GemMaterialConfigManager gemMaterialConfigManager = SingletonManager.getInstance().configManager.getGemMaterialConfigManager();

    public boolean isLeftClick(Action a) {
        return a.equals(Action.LEFT_CLICK_BLOCK) || a.equals(Action.LEFT_CLICK_AIR);
    }

    public Material[] ItemStackToMaterial(ItemStack[] items) {
        Material[] transformed = new Material[items.length];
        int i = 0;
        for (ItemStack item : items) {
            if (item == null) {
                transformed[i] = null;
            } else {
                transformed[i] = item.getType();
            }
            i++;
        }
        return transformed;
    }

    public boolean checkIfEqualMaterial(Material[] a1, Material[] a2) {
        int i = 0;
        for (Material mat : a1) {
            if (mat == null) {
                return false;
            }
            if (Objects.equals(mat.toString(), a2[i].toString())) {
                return false;
            }
            i++;
        }
        return true;
    }

    public ArrayList<Block> getSquareOutlineCoordinates(Player player, int radius) {
        // Get the player's current location.
        Location center = player.getLocation();

        // Calculate the start and end coordinates of the square.
        int startX = center.getBlockX() - radius;
        int endX = center.getBlockX() + radius;
        int startZ = center.getBlockZ() - radius;
        int endZ = center.getBlockZ() + radius;

        // Create an array to store the coordinates of the square outline.
        ArrayList<Block> blocks = new ArrayList<>();

        // Iterate over the start and end coordinates to generate the square outline
        // coordinates.
        for (int x = startX; x <= endX; x++) {
            blocks.add(new Location(center.getWorld(), x, center.getBlockY(), startZ).getBlock());
            blocks.add(new Location(center.getWorld(), x, center.getBlockY(), endZ).getBlock());
        }
        for (int z = startZ + 1; z <= endZ - 1; z++) {
            blocks.add(new Location(center.getWorld(), startX, center.getBlockY(), z).getBlock());
            blocks.add(new Location(center.getWorld(), endX, center.getBlockY(), z).getBlock());
        }

        // Return the array of square outline coordinates.
        return blocks;
    }

    public ArrayList<Block> getSquareOutlineAirBlocks(Player player, int radius) {
        // Get the player's current location.
        Location center = player.getLocation();

        // Calculate the start and end coordinates of the square.
        int startX = center.getBlockX() - radius;
        int endX = center.getBlockX() + radius;
        int startZ = center.getBlockZ() - radius;
        int endZ = center.getBlockZ() + radius;

        // Create an array to store the coordinates of the square outline.
        ArrayList<Block> blocks = new ArrayList<>();

        // Iterate over the start and end coordinates to generate the square outline
        // coordinates.
        for (int x = startX; x <= endX; x++) {
            blocks.add(new Location(center.getWorld(), x, center.getBlockY(), startZ).getBlock());
            blocks.add(new Location(center.getWorld(), x, center.getBlockY(), endZ).getBlock());
        }
        for (int z = startZ + 1; z <= endZ - 1; z++) {
            blocks.add(new Location(center.getWorld(), startX, center.getBlockY(), z).getBlock());
            blocks.add(new Location(center.getWorld(), endX, center.getBlockY(), z).getBlock());
        }

        // Check for air blocks
        blocks.removeIf(block -> !block.isEmpty());

        // Return the array of square outline coordinates.
        return blocks;
    }

    public boolean hasAtLeastXAmount(Player player, Material material, int x) {
        int count = player.getInventory().all(material).values().stream()
                .mapToInt(ItemStack::getAmount)
                .sum();
        if (player.getInventory().getItemInOffHand().getType() == material)
            count += player.getInventory().getItemInOffHand().getAmount();
        return count >= x;
    }

    public ArrayList<ItemStack> getUserGems(Player plr) {
        ArrayList<ItemStack> gems = new ArrayList<>();
        if (!hasAtLeastXAmount(plr, Material.EMERALD, 1))
            return gems;
        for (ItemStack item : plr.getInventory().getContents()) {
            if (gemManager.isGem(item)) {
                gems.add(item);
            }
        }
        return gems;

    }

    public boolean hasAtLeastXAmountOfGems(Player player, int x) {
        int totalCount = 0;
        // Iterate over the player's inventory once
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && gemManager.isGem(item)) {
                totalCount += item.getAmount();
                // Early termination if the count reaches or exceeds 'x'
                if (totalCount >= x) {
                    return true;
                }
            }
        }
        // Check the offhand item separately
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && gemManager.isGem(offHandItem)) {
            totalCount += offHandItem.getAmount();
        }
        return totalCount >= x;
    }
}
