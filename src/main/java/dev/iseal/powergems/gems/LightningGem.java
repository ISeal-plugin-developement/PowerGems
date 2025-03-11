package dev.iseal.powergems.gems;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
    protected void rightClick(Player plr) {
        Block possibleTarget = plr.getTargetBlock(null, 90);
        if (possibleTarget == null) {
            plr.sendMessage(I18N.getTranslation("MUST_LOOK_AT_BLOCK"));
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
    protected void leftClick(Player plr) {
        Location playerLocation = plr.getLocation();
        World world = playerLocation.getWorld();
        plr.setVelocity(playerLocation.getDirection().multiply(5));
        world.spawnParticle(Particle.FLASH, playerLocation, 100, 0, 0, 0, 0.2);
    }

    @Override
    protected void shiftClick(Player plr) {
        Location playerLocation = plr.getLocation();
        World world = playerLocation.getWorld();
        world.playSound(playerLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
        for (Entity e : world.getNearbyEntities(playerLocation, 5, 5, 5)) {
            if (e instanceof LivingEntity) {
                if (e != plr) {
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 0));
                }
            }
        }
    }

    @Override
    public PotionEffectType getEffect() {
        return PotionEffectType.SPEED;
    }
}

