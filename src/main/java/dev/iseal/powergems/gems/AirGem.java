package dev.iseal.powergems.gems;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import dev.iseal.powergems.gems.powerClasses.tasks.AirGemPull;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class AirGem extends Gem {

    Utils utils = SingletonManager.getInstance().utils;

    public AirGem() {
        super("Air");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        double maxDistance = 20.0 * (level / 2.0 + 1.0);

        List<Entity> nearbyEntities = plr.getNearbyEntities(maxDistance, maxDistance, maxDistance);
        LivingEntity lookingAt = null;
        double closestDistance = maxDistance;

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof LivingEntity) || entity.getUniqueId().equals(plr.getUniqueId())) {
                continue;
            }

            if (!plr.hasLineOfSight(entity)) {
                continue;
            }

            double distance = plr.getLocation().distance(entity.getLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                lookingAt = (LivingEntity) entity;
            }
        }

        // If an entity is in line of sight, pull it
        if (lookingAt != null) {
            Location targetLoc = lookingAt.getLocation();
            utils.spawnLineParticles(
                    plr.getEyeLocation(),
                    targetLoc,
                    200, 200, 255,
                    0.3D,
                    null,
                    2
            );
            AirGemPull puller = new AirGemPull(lookingAt, plr, 0.1 + (level * 0.05), 100 + ((level - 1) * 20), level);
            puller.start();
            if (lookingAt instanceof Player tPlayer) {
                tPlayer.damage(2.0 + level, plr);
                tPlayer.playSound(tPlayer.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 0.5f);
                tPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100 + ((level - 1) * 20), 1));
                tPlayer.sendMessage(I18N.translate("IN_AIR_PULL"));
            }

            double range = 5.0;
            List<LivingEntity> others = targetLoc.getWorld().getNearbyEntities(targetLoc, range, range, range).stream()
                    .filter(e -> e instanceof LivingEntity && !e.getUniqueId().equals(plr.getUniqueId()))
                    .map(e -> (LivingEntity) e)
                    .toList();
            for (LivingEntity ent : others) {
                ent.sendMessage(I18N.translate("NEAR_PULL_EFFECT"));
            }
        }
    }

    @Override
    protected void leftClick(Player plr) {
        Location playerLocation = plr.getLocation();
        double radius = 10.0 * (level / 2D); // Radius of effect
        double power = 2.5 + level; // Strength of the burst

        List<LivingEntity> nearbyEntities = plr.getWorld().getNearbyEntities(playerLocation, radius, radius, radius).stream()
                .filter(entity -> entity instanceof LivingEntity && entity.getUniqueId() != plr.getUniqueId())
                .map(entity -> (LivingEntity) entity)
                .toList();

        for (LivingEntity entity : nearbyEntities) {
            entity.setVelocity(entity.getVelocity().add(new Vector(0, power, 0)));
            if (entity instanceof Player player) {
                player.damage(power, plr);
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
    }

    @Override
    protected void shiftClick(Player plr) {
        double distance = 6 * (level / 2.0);
        Location location = plr.getLocation();
        Vector direction = location.getDirection().normalize();
        AreaEffectCloud effect = (AreaEffectCloud) plr.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
        effect.setDuration(60);
        effect.setRadius(1.0f);
        effect.setParticle(Particle.SMOKE_LARGE);
        effect.setColor(Color.BLACK);
        plr.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
        plr.setVelocity(direction.multiply(distance));
    }

    @Override
    public PotionEffectType getEffect() {
        return PotionEffectType.SLOW_FALLING;
    }
}