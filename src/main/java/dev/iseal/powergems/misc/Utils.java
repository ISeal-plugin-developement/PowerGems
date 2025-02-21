package dev.iseal.powergems.misc;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.tasks.SpawnColoredLineTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Utils {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    public Location getXBlocksInFrontOfPlayer(Location startLocation, Vector dir, int x) {
        RayTraceResult result = startLocation.getWorld().rayTraceBlocks(startLocation, dir, x);
        Location targetLocation;

        // If a target block is found, use its location; otherwise, use the last block in the raycast
        if (result != null && result.getHitBlock() != null) {
            targetLocation = result.getHitBlock().getLocation();
        } else {
            targetLocation = startLocation.add(startLocation.getDirection().multiply(x));
        }
        return targetLocation;
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

    public boolean hasAtLeastXAmountOfGems(Player player, int x) {
        return hasAtLeastXAmountOfGems(player.getInventory(), x, player.getInventory().getItemInOffHand());
    }

    public void spawnFancyParticlesInLine(Location start, Location target, int lineRed, int lineGreen, int lineBlue, int circleRed, int circleGreen, int circleBlue, double lineInterval, double circleInterval, double circleParticleInterval, double circleRadius, Consumer<Location> lineConsumer, Consumer<Location> circleConsumer, int repeatAmount) {
        SpawnColoredLineTask task = new SpawnColoredLineTask();
        task.start = start.clone();
        task.target = target.clone();
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
        task.repeatAmount = repeatAmount;
        task.init();
        task.runTaskTimer(PowerGems.getPlugin(), 0, 1);
    }

    public void spawnLineParticles(Location start, Location target, int red, int green, int blue, double interval, Consumer<Location> consumer, int repeatAmount) {
        SpawnColoredLineTask task = new SpawnColoredLineTask();
        task.start = start.clone();
        task.target = target.clone();
        task.lineRed = red;
        task.lineGreen = green;
        task.lineBlue = blue;
        task.lineInterval = interval;
        task.lineConsumer = consumer;
        task.spawnLines = true;
        task.spawnCircles = false;
        task.repeatAmount = repeatAmount;
        task.init();
        task.runTaskTimer(PowerGems.getPlugin(), 0, 1);
    }

    /*
        * Returns the direction vector of the player.
        * @param rotX The rotation on the x-axis (the yaw).
        * @param rotY The rotation on the y-axis (the pitch).
        *
        * @return The direction vector of the player.
     */
    public Vector getDirection(double rotX, double rotY) {
        Vector vector = new Vector();

        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));

        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

        return vector;
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
        // Get a random locaion in a 3x3x2 area around the player, with the player at its center
        double x = playerLocation.getX() + Math.random() * 1.5 - 0.75;
        double y = playerLocation.getY() + Math.random() * 2 - 1;
        double z = playerLocation.getZ() + Math.random() * 1.5 - 0.75;
        return new Location(player.getWorld(), x, y, z);
    }

    public void addPreciseEffect(Player player, PotionEffectType type, int duration, int amplifier) {
        PotionEffect currentEffect = player.getPotionEffect(type);

        if (currentEffect == null) {
            player.addPotionEffect(new PotionEffect(type, duration, amplifier));
            return;
        }

        int currentAmplifier = currentEffect.getAmplifier();
        int currentDuration = currentEffect.getDuration();

        player.removePotionEffect(type);
        player.addPotionEffect(new PotionEffect(type, duration+currentDuration, Math.max(amplifier, currentAmplifier)));
    }

}
