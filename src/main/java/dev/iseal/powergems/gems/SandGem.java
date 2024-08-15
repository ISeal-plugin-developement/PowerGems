package dev.iseal.powergems.gems;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import java.util.HashMap;
import java.util.List;

public class SandGem extends Gem {

    private final Utils utils = SingletonManager.getInstance().utils;
    private final GeneralConfigManager gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        RayTraceResult result = plr.getWorld().rayTrace(plr.getEyeLocation(), plr.getEyeLocation().getDirection(), 200D,
                FluidCollisionMode.ALWAYS, true, 1, entity -> !entity.equals(plr) && entity instanceof Player);
        if (result == null || result.getHitEntity() == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You need to aim at a player to do that");
            return;
        }
        Player target = (Player) result.getHitEntity();
        if (target == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You must be looking at a player to do that");
            return;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80 + (level * 40), 1 + level));
        target.setFoodLevel(Math.max(0, target.getFoodLevel() - 5 + (level * 2)));
    }

    @Override
    protected void leftClick(Player plr) {
        // Perform a raycast to find the target block
        RayTraceResult result = plr.getWorld().rayTraceBlocks(plr.getEyeLocation(), plr.getEyeLocation().getDirection(), 100);
        Location targetLocation;

        // If a target block is found, use its location; otherwise, use the last block in the raycast
        if (result != null && result.getHitBlock() != null) {
            targetLocation = result.getHitBlock().getLocation();
        } else {
            targetLocation = plr.getEyeLocation().add(plr.getEyeLocation().getDirection().multiply(100));
        }

        // Call the utils.drawFancyLine method
        utils.spawnFancyParticlesInLine(
                plr.getEyeLocation(),
                targetLocation,
                255, 204, 0, // Semi dark yellow for line
                204, 153, 0, // Darker yellow for circles
                0.4, // Line interval
                5-level/2, // Circle interval
                0.2, // Circle particle interval
                1+level/2, // Circle radius
                plr,
                loc -> {
                    double radius = 1;
                    List<Entity> nearbyEntities = (List<Entity>) loc.getWorld().getNearbyEntities(loc, radius, radius, radius);
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof Player targetPlr && !entity.getUniqueId().equals(plr.getUniqueId())) {
                            targetPlr.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 1));
                            targetPlr.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
                        }
                    }
                }, //line consumer
                loc -> {
                    double radius = 1 + level / 2;
                    List<Entity> nearbyEntities = (List<Entity>) loc.getWorld().getNearbyEntities(loc, radius, radius, radius);
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof Player targetPlr && !entity.getUniqueId().equals(plr.getUniqueId())) {
                            targetPlr.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 200, 1));
                            targetPlr.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
                        }
                    }
                }
        );
    }

    @Override
    protected void shiftClick(Player plr) {
        if (sm.sandMoveListen.hasToRemoveFrom(plr.getUniqueId())) {
            plr.sendMessage(ChatColor.DARK_RED + "You already have a trap active");
            return;
        }

        Location targetLocation = plr.getLocation().clone().add(0,-1,0);

        int tries = 0;
        while (gcm.isBlockedReplacingBlock(targetLocation.getBlock()) && tries < 70) {
            targetLocation.add(0, -1, 0);
            tries++;
        }

        HashMap<Block, Material> toReplace = new HashMap<>();

        utils.generateSquare(targetLocation, level*2).forEach(block -> {
            if (!gcm.isBlockedReplacingBlock(block)
                    && block.getRelative(BlockFace.UP).isEmpty()
                    && block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).isEmpty()
                    && !block.isEmpty() && !block.getRelative(BlockFace.DOWN).isEmpty() ) {

                Material oldMaterial = targetLocation.getBlock().getType();
                sm.sandMoveListen.addToList(block, plr.getUniqueId());
                toReplace.put(block, oldMaterial);
            }
        });

        toReplace.forEach((block, material) -> {
            block.setType(Material.SAND);
        });

        sm.sandMoveListen.addToRemoveList(plr.getUniqueId(), toReplace);

        Bukkit.getScheduler().runTaskLater(PowerGems.getPlugin(), () -> {
            sm.sandMoveListen.removeFromList(plr.getUniqueId());
        }, 50L*level);
    }

    @Override
    public void Particle particle(Player plr){
        return Particle.FIREWORK;
    }
}
