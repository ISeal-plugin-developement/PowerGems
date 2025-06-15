package dev.iseal.powergems.gems;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class WaterGem extends Gem {

    public WaterGem() {
        super("Water");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        if (plr.getEyeLocation().getBlock().getType() != Material.WATER
                || plr.getLocation().getBlock().getType() != Material.WATER)
            return;
        plr.setVelocity(plr.getVelocity().add(plr.getLocation().getDirection().multiply(level / 2)));
        World world = plr.getWorld();
        world.spawnParticle(Particle.BUBBLE_COLUMN_UP, plr.getLocation(), 5 * level);
    }

    @Override
    protected void leftClick(Player plr, int level) {
        Location loc = plr.getLocation();
        loc.setY(loc.getY() - 1);
        int halfRadius = level * 2;
        // Itinerate in a square around the location
        for (int x = -halfRadius; x <= halfRadius; x++) {
            for (int z = -halfRadius; z <= halfRadius; z++) {
                Location pos = new Location(loc.getWorld(), loc.getX() + x, loc.getY(), loc.getZ() + z);
                Block block = pos.getBlock();
                if (block.getType() != Material.FARMLAND)
                    continue;
                Farmland farmland = (Farmland) block.getBlockData();
                farmland.setMoisture(farmland.getMaximumMoisture());
                block.setBlockData(farmland);
                block.applyBoneMeal(BlockFace.UP);
            }
        }
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        //Disable shift click in the nether
        if(plr.getWorld().getEnvironment() == World.Environment.NETHER) {
            plr.sendMessage(I18N.translate("WATER_GEM_SHIFT_DISABLED_NETHER"));
            return;
        }
        // Get the player's position
        Location playerPos = plr.getLocation();
        int halfRadius = 3 + level / 2;

        // Calculate the start and end positions of the cube
        int startX = playerPos.getBlockX() - halfRadius;
        int startY = playerPos.getBlockY();
        int startZ = playerPos.getBlockZ() - halfRadius;
        int endX = playerPos.getBlockX() + halfRadius;
        int endY = playerPos.getBlockY() + halfRadius*2;
        int endZ = playerPos.getBlockZ() + halfRadius;

        // Iterate over the cube
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    // Set the block to water
                    Location pos = new Location(plr.getWorld(), x, y, z);
                    Block block = pos.getBlock();
                    if (!block.isEmpty())
                        continue;
                    block.setType(Material.WATER);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(PowerGems.getPlugin(), () -> {
                        if (block.getType() == Material.WATER)
                            block.setType(Material.AIR);
                    }, 400+level* 40L);
                }
            }
        }
        plr.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 500+level*100, 2));
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Propel yourself forward in water, creating bubbles.");
        lore.add(ChatColor.WHITE + "Shift click: Create a temporary water cube around you, granting Dolphin's Grace.");
        lore.add(ChatColor.WHITE + "Left click: Moisturize farmland blocks around you.");
        lore.add(ChatColor.BLUE + "Passive: Power up yourself with water");
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.CONDUIT_POWER;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }
}

