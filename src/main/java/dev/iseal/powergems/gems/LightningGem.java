package dev.iseal.powergems.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class LightningGem extends Gem {

    public LightningGem() {
        super("Lightning");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        Block possibleTarget = plr.getTargetBlock(null, 90);
        if (possibleTarget == null) {
            plr.sendMessage(I18N.translate("MUST_LOOK_AT_BLOCK"));
            return;
        }
        Location targetLocation = possibleTarget.getLocation();
        World plrWorld = plr.getWorld();
        plrWorld.strikeLightning(targetLocation);
        for (Entity e : plrWorld.getNearbyEntities(targetLocation, 5, 5, 5)) {
            if (e instanceof LivingEntity) {
                plrWorld.strikeLightning(e.getLocation());
            }
        }
    }

    @Override
    protected void leftClick(Player plr, int level) {
        Location playerLocation = plr.getLocation();
        World world = playerLocation.getWorld();
        plr.setVelocity(playerLocation.getDirection().multiply(5));
        world.spawnParticle(Particle.FLASH, playerLocation, 100, 0, 0, 0, 0.2);
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        Location playerLocation = plr.getLocation();
        World world = playerLocation.getWorld();
        world.playSound(playerLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        for (Entity e : world.getNearbyEntities(playerLocation, 5, 5, 5)) {
            if (e instanceof LivingEntity && e != plr) {
                ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 0));
            }
        }
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.SPEED;
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
                + "Right click: Strikes lightning at the target location and nearby entities, damaging them.");
        lore.add(ChatColor.WHITE
                + "Shift click: Emits a thunder sound effect and applies a glowing potion effect to nearby entities, excluding the player.");
        lore.add(ChatColor.WHITE + "Left click: Launches the player forward in the direction rail.");
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.ELECTRIC_SPARK;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
