package me.iseal.powergems.gems;

import me.iseal.powergems.misc.Gem;
import org.bukkit.*;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class AirGem extends Gem {

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        int distance = 5 * (level / 2); // Maximum distance between the players
        double force = 1.5 + level; // Strength of the pull
        RayTraceResult result = plr.getWorld().rayTrace(plr.getEyeLocation(), plr.getEyeLocation().getDirection(),
                distance, FluidCollisionMode.ALWAYS, true, 1,
                entity -> !entity.equals(plr) && entity instanceof Player);
        if (result == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You need to aim at a player to do that");
            return;
        }
        Player targetplr = (Player) result.getHitEntity();
        if (targetplr == null) {
            plr.sendMessage(ChatColor.DARK_RED + "You need to aim at a player to do that");
            return;
        }
        Location playerLocation = plr.getLocation();
        Location targetLocation = targetplr.getLocation();
        Vector direction = playerLocation.subtract(targetLocation).toVector().normalize();
        targetplr.setVelocity(targetplr.getVelocity().add(direction.multiply(force)));
    }

    @Override
    protected void leftClick(Player plr) {
        Location playerLocation = plr.getLocation();
        double radius = 5.0 * (level / 2); // Radius of effect
        double power = 2.5 + (level / 2); // Strength of the burst

        plr.getWorld().getEntities().stream()
                .filter(entity -> entity.getLocation().distance(playerLocation) <= radius)
                .forEach(entity -> {
                    if (entity != plr) {
                        entity.setVelocity(entity.getVelocity().add(new Vector(0, power, 0)));
                        if (entity instanceof LivingEntity) {
                            ((LivingEntity) entity).damage(power);
                        }
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
