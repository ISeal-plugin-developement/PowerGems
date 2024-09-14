package dev.iseal.powergems.gems;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class AirGem extends Gem {

    Utils utils = SingletonManager.getInstance().utils;

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        Location eyeLocation = plr.getEyeLocation().clone();
        Location targetLocation = utils.getXBlocksInFrontOfPlayer(plr.getEyeLocation(), plr.getLocation().getDirection(), 100);

        utils.spawnLineParticles(
                eyeLocation,
                targetLocation,
                255,
                255,
                255,
                0.4D,
                (location) -> {
                    location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5)
                            .stream()
                            .filter(entity -> entity instanceof LivingEntity && entity.getUniqueId() != plr.getUniqueId())
                            .forEach(entity -> {
                                entity.setVelocity(entity.getVelocity().add(new Vector(0, 2.5 + level, 0)));
                                if (entity instanceof Player player) {
                                    player.damage(2.5 + level, plr);
                                    player.playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                                }
                            });
                },
                3
                );
    }

    @Override
    protected void leftClick(Player plr) {
            Location playerLocation = plr.getLocation();
            double radius = 10.0 * (level / 2D); // Radius of effect
            double power = 2.5 + level; // Strength of the burst

            plr.getWorld().getNearbyEntities(playerLocation, radius, radius, radius)
                    .stream()
                    .filter(entity -> entity instanceof LivingEntity && entity.getUniqueId() != plr.getUniqueId())
                    .forEach(entity -> {
                        entity.setVelocity(entity.getVelocity().add(new Vector(0, power, 0)));
                        if (entity instanceof Player player) {
                            player.damage(power, plr);
                            player.playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                        }
                    });
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
}
