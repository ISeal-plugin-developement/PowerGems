package dev.iseal.powergems.misc;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.powergems.tasks.SpawnColoredLineTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Utils {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;


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

    /*
        * Returns a list of all the gems in the player's inventory.
        *
        * @param plr The player to check for gems.
        *
        * @return A list of all the gems in the player's inventory.
     */
    public ArrayList<ItemStack> getUserGems(Player plr) {
        ArrayList<ItemStack> gems = new ArrayList<>();
        if (!hasAtLeastXAmountOfGems(plr, 1))
            return gems;
        for (ItemStack item : plr.getInventory().getContents()) {
            if (gemManager.isGem(item)) {
                gems.add(item);
            }
        }
        return gems;

    }

    public boolean hasAtLeastXAmountOfGems(Inventory inv, int x, ItemStack... moreToCheck){
        int totalCount = 0;
        // Iterate over the player's inventory once
        for (ItemStack item : inv.getContents()) {
            if (item != null && gemManager.isGem(item)) {
                totalCount += item.getAmount();
                // Early termination if the count reaches or exceeds 'x'
                if (totalCount >= x) {
                    return true;
                }
            }
        }

        for (ItemStack item : moreToCheck) {
            if (item != null && gemManager.isGem(item)) {
                totalCount += item.getAmount();
                // Early termination if the count reaches or exceeds 'x'
                if (totalCount >= x) {
                    return true;
                }
            }
        }

        return totalCount >= x;
    }

    public static Set<Class<? extends AbstractConfigManager>> findAllClassesInPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(AbstractConfigManager.class);
    }

    public boolean hasAtLeastXAmountOfGems(Player player, int x) {
        return hasAtLeastXAmountOfGems(player.getInventory(), x, player.getInventory().getItemInOffHand());
    }

    public void spawnFancyParticlesInLine(Location start, Location target, int lineRed, int lineGreen, int lineBlue, int circleRed, int circleGreen, int circleBlue, double lineInterval, double circleInterval, double circleParticleInterval, double circleRadius, Player spawningPlayer, Consumer<Location> lineConsumer, Consumer<Location> circleConsumer) {
        SpawnColoredLineTask task = new SpawnColoredLineTask();
        task.start = start;
        task.target = target;
        task.lineRed = lineRed;
        task.lineGreen = lineGreen;
        task.lineBlue = lineBlue;
        task.circleRed = circleRed;
        task.circleGreen = circleGreen;
        task.circleBlue = circleBlue;
        task.lineInterval = lineInterval;
        task.circleInterval = circleInterval;
        task.circleParticleInterval = circleParticleInterval;
        task.circleRadius = circleRadius;
        task.lineConsumer = lineConsumer;
        task.circleConsumer = circleConsumer;
        task.spawnLines = true;
        task.spawnCircles = true;
        task.spawningPlayer = spawningPlayer;
        task.init();
        task.runTaskTimer(PowerGems.getPlugin(), 0, 1);
    }

    public List<Block> generateSquare(Location center, int size) {
        List<Block> blocks = new ArrayList<>();
        int halfSize = size / 2;

        for (int x = -halfSize; x <= halfSize; x++) {
            for (int z = -halfSize; z <= halfSize; z++) {
                Location loc = center.clone().add(x, 0, z);
                blocks.add(loc.getBlock());
            }
        }

        return blocks;
    }

    public Location getRandomLocationCloseToPlayer(Player player) {
        Location playerLocation = player.getLocation();
        double x = playerLocation.getX() +  Math.random() + 0.25D;
        double z = playerLocation.getZ() + Math.random() + 0.25D;
        return new Location(playerLocation.getWorld(), x, playerLocation.getY(), z);
    }

}
