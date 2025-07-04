package dev.iseal.powergems.gems;

import dev.iseal.powergems.managers.Addons.CombatLogX.CombatLogXAddonManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.gems.powerClasses.tasks.FireballPowerDecay;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;

import java.util.ArrayList;
import org.bukkit.ChatColor;

public class FireGem extends Gem {

    public FireGem() {
        super("Fire");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
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
    protected void leftClick(Player plr, int level) {
        Location playerLocation = plr.getLocation();
        World world = plr.getWorld();
        plr.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1));
        world.createExplosion(playerLocation, level + 1f, true, false);
        for (Entity entity : plr.getNearbyEntities(level+3, level+3, level+3)) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(5 * ((double) level / 2), plr);
                entity.setFireTicks(100);
                if (entity instanceof Player attackedPlayer) {
                    if (PowerGems.isEnabled("CombatLogX") && gcm.isCombatLogXEnabled())
                        CombatLogXAddonManager.getInstance().setInFight(plr, attackedPlayer);
                }
            }
        }
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        if (sm.tempDataManager.chargingFireball.containsKey(plr)) {
            plr.sendMessage(I18N.translate("FIREBALL_ALREADY_CHARGING"));
            return;
        }

        FireballPowerDecay task = new FireballPowerDecay();
        task.plr = plr;
        task.level = level;
        task.currentPower = 50;
        
        // Cache
        sm.tempDataManager.chargingFireball.put(plr, task);
        
        task.runTaskTimer(PowerGems.getPlugin(), 0L, 1L);
        
        Location plrEyeLoc = plr.getEyeLocation();
        plrEyeLoc.add(plr.getLocation().getDirection().multiply(10));
        plrEyeLoc.add(0, -0.5, 0);
        plr.getWorld().spawnParticle(Particle.ASH, plrEyeLoc, 10, 0, 0, 0, 0);
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.FIRE_RESISTANCE;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE
                + "Right click: Creates a fiery aura around the player, granting fire resistance and igniting nearby air blocks.");
        lore.add(ChatColor.WHITE
                + "Shift click: Triggers a powerful explosion at the player's location, damaging nearby entities and applying fire damage.");
        lore.add(ChatColor.WHITE
                + "Left click: Launches a fireball in the direction the player is facing, causing an explosion upon impact.");
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.FLAME;
    }
}
