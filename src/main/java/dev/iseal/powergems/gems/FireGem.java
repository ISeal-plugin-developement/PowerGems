package dev.iseal.powergems.gems;

import dev.iseal.powergems.managers.Addons.CombatLogX.CombatLogXAddonManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
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
import net.kyori.adventure.text.Component;

import java.util.ArrayList;


public class FireGem extends Gem {
    public FireGem() {
        super("Fire");
    }

    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;
    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    /**
     * Right click: Grants the player Fire Resistance,
     * and ignites nearby blocks.
     *
     * @param plr The player who used the gem.
     * @param level The gem level.
     */
    @Override
    protected void rightClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
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
        });
    }

    /**
     * Left click: Creates an explosion at the player's location,damaging them,
     * while also giving the user player Fire Resistance and Resistance.
     * @param plr The player who used the gem.
     * @param level The gem level.
     */
    @Override
    protected void leftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location playerLocation = plr.getLocation();
            World world = plr.getWorld();
            plr.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 10, 5));
            plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1));
            world.createExplosion(playerLocation, level + 1f, true, false);
            for (Entity entity : plr.getNearbyEntities(level+3, level+3, level+3)) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).damage(5 * ((double) level / 2), plr);
                    entity.setFireTicks(100);
                    if (entity instanceof Player attackedPlayer && PowerGems.isEnabled("CombatLogX") && gcm.isCombatLogXEnabled()) {
                        CombatLogXAddonManager.getInstance().setInFight(plr, attackedPlayer);
                    }
                }
            }
        });
    }
    /**
     * Shift click: Shoots a fireball,
     * at the direction the player is facing.
     *
     * @param plr The player who used the gem.
     * @param level The gem level.
     */
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

        schedulerWrapper.scheduleRepeatingTaskForEntity(plr, task, 0L, 1L, null);

        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location plrEyeLoc = plr.getEyeLocation();
            plrEyeLoc.add(plr.getLocation().getDirection().multiply(10));
            plrEyeLoc.add(0, -0.5, 0);
            plr.getWorld().spawnParticle(Particle.ASH, plrEyeLoc, 10, 0, 0, 0, 0);
        });

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
        lore.add(Component.text("Level %level%", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Abilities", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Right click: Creates a fiery aura around the player, granting fire resistance and igniting nearby air blocks.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Shift click: Triggers a powerful explosion at the player's location, damaging nearby entities and applying fire damage.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Left click: Launches a fireball in the direction the player is facing, causing an explosion upon impact.", NamedTextColor.WHITE).toString());
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.FLAME;
    }
}
