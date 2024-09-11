package dev.iseal.powergems.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FireGem extends Gem {

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        Location playerLocation = plr.getLocation();
        World world = plr.getWorld();
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1));
        int radius = 8 * (level / 2);
        for (int x = playerLocation.getBlockX() - radius; x <= playerLocation.getBlockX() + radius; x++) {
            for (int z = playerLocation.getBlockZ() - radius; z <= playerLocation.getBlockZ() + radius; z++) {
                Block block = world.getBlockAt(x, playerLocation.getBlockY(), z);
                if (block.getType() == Material.AIR || block.getType() == Material.SNOW && !block.isLiquid()) {
                    block.setType(Material.FIRE);
                }
            }
        }
    }

    @Override
    protected void leftClick(Player plr) {
        Location playerLocation = plr.getLocation();
        World world = plr.getWorld();
        plr.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1));
        world.createExplosion(playerLocation, level + 1f, true, false);
        for (Entity entity : plr.getNearbyEntities(level+3, level+3, level+3)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(5 * (level / 2), plr);
                entity.setFireTicks(100);
            }
        }
    }

    @Override
    protected void shiftClick(Player plr) {
        if (sm.tempDataManager.chargingFireball.containsKey(plr)) {
            plr.sendMessage(ChatColor.DARK_RED + "You are already charging a fireball.");
            return;
        }
        Location plrEyeLoc = plr.getEyeLocation();
        World world = plr.getWorld();
        plrEyeLoc.add(0, -0.5, 0);

        // Spawn particles that move to certain location
        for (int i = 0; i < 10; i++) {
            world.spawnParticle(Particle.ASH, plrEyeLoc, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public Particle particle(Player plr) {
        return org.bukkit.Particle.VILLAGER_ANGRY;
    }
}
