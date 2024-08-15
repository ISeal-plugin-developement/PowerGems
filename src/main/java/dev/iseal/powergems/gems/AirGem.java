package dev.iseal.powergems.gems;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.listeners.powerListeners.AirListeners;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.WrapperObjects.UUIDTagType;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class AirGem extends Gem {

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        double force = 1.5 + level; // Strength of the pull
        Location playerLocation = plr.getLocation();
        Arrow arrow = plr.launchProjectile(Arrow.class, playerLocation.getDirection().multiply(force));
        PersistentDataContainer container = arrow.getPersistentDataContainer();
        UUID randomID = UUID.randomUUID();
        container.set(new NamespacedKey(PowerGems.getPlugin(), "is_gem_projectile"), PersistentDataType.BOOLEAN, true);
        container.set(new NamespacedKey(PowerGems.getPlugin(), "gem_owner"), PersistentDataType.STRING, "Air");
        container.set(new NamespacedKey(PowerGems.getPlugin(), "leash_entity"), new UUIDTagType(), randomID);
        arrow.setInvulnerable(true);
        arrow.setSilent(true);
        Silverfish leash = (Silverfish) plr.getWorld().spawnEntity(playerLocation, EntityType.SILVERFISH);
        leash.setAI(false);
        leash.setInvulnerable(true);
        leash.setSilent(true);
        leash.setInvisible(true);
        leash.setLeashHolder(plr);
        AirListeners.getInstance().addLeashEntity(randomID, leash);
    }

    @Override
    protected void leftClick(Player plr) {
        Location playerLocation = plr.getLocation();
        double radius = 5.0 * (level / 2D); // Radius of effect
        double power = 2.5 + level; // Strength of the burst

        plr.getWorld().getNearbyEntities(playerLocation, radius, radius, radius)
                .stream()
                .filter(entity -> entity instanceof Player && entity.getUniqueId() != plr.getUniqueId())
                .forEach(entity -> {
                    Player player = (Player) entity;
                    player.setVelocity(entity.getVelocity().add(new Vector(0, power, 0)));
                    player.damage(power, plr);
                    player.playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
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

    @Override
    public Particle particle(Player plr) {
        return Particle.CLOUD;
    }
}
