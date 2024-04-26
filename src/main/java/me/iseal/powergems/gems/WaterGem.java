package me.iseal.powergems.gems;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.iseal.powergems.misc.Gem;
import me.iseal.powergems.Main;

public class WaterGem extends Gem {
    
    @Override
    public void call(Action act, Player plr, ItemStack item){
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        if (plr.getEyeLocation().getBlock().getType() != Material.WATER || plr.getLocation().getBlock().getType() != Material.WATER) return;
        plr.setVelocity(plr.getVelocity().add(plr.getLocation().getDirection().multiply(level/2)));
        World world = plr.getWorld();
        world.spawnParticle(Particle.BUBBLE_COLUMN_UP, plr.getLocation(), 5*level);
    }

    @Override
    protected void leftClick(Player plr) {
        System.out.println("Not implemeted yet");
        
    }

    @Override
    protected void shiftClick(Player plr) {
        // Get the player's position
        Location playerPos = plr.getLocation();
        int halfRadius = 1+level/2;

        // Calculate the start  and end positions of the cube
        int startX = playerPos.getBlockX() - halfRadius;
        int startY = playerPos.getBlockY() - halfRadius;
        int startZ = playerPos.getBlockZ() - halfRadius;
        int endX = playerPos.getBlockX() + halfRadius;
        int endY = playerPos.getBlockY() + halfRadius;
        int endZ = playerPos.getBlockZ() + halfRadius;

        // Iterate over the cube
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    // Set the block to water
                    Location pos = new Location(plr.getWorld(), x, y, z);
                    Block block = pos.getBlock();
                    if (!block.isEmpty()) continue;
                    block.setType(Material.WATER);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                        block.setType(Material.AIR);
                    }, 400);
                }
            }
        }
        plr.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 500, 2));
    }

}
