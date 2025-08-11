package dev.iseal.powergems.gems;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.gems.powerClasses.tasks.AirGemPull;
import dev.iseal.powergems.managers.Addons.CombatLogX.CombatLogXAddonManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class AirGem extends Gem {
    private final Utils utils = SingletonManager.getInstance().utils;
    private final GeneralConfigManager gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    public AirGem() {
        super("Air");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location playerLocation = plr.getLocation();
            Vector playerDirection = playerLocation.getDirection();
            double range = 20.0 * (level / 2.0 + 1.0);
            double pullStrength = 0.1 + (level * 0.05);
            int pullDuration = 100 + ((level - 1) * 20);

            plr.getWorld().getNearbyEntities(playerLocation, range, range, range).stream()
                    .filter(entity -> entity instanceof LivingEntity && entity.getUniqueId() != plr.getUniqueId())
                    .filter(entity -> {
                        Vector directionToEntity = entity.getLocation().toVector().subtract(playerLocation.toVector());
                        double dot = directionToEntity.normalize().dot(playerDirection);
                        return dot > 0.5; // ~60 degree cone
                    })
                    .forEach(entity -> {
                        utils.spawnLineParticles(
                                plr.getEyeLocation(),
                                entity.getLocation(),
                                200, 200, 255, // Light blue
                                0.3D,
                                null,
                                2
                        );

                        AirGemPull puller = new AirGemPull(entity, plr, pullStrength, pullDuration);
                        puller.start();

                        if (entity instanceof Player targetPlayer) {
                            targetPlayer.damage(2.0 + level, plr);
                            targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0f, 0.5f);
                            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, pullDuration, 1));

                            if (PowerGems.isEnabled("CombatLogX") && gcm.isCombatLogXEnabled())
                                CombatLogXAddonManager.getInstance().setInFight(plr, targetPlayer);

                            targetPlayer.sendMessage(I18N.translate("IN_AIR_PULL"));
                        }
                    });
        });
    }

    @Override
    protected void leftClick(Player plr, int level) {
        // Schedule air burst in player's region for Folia compatibility
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location playerLocation = plr.getLocation();
            double radius = 10.0 * (level / 2D);
            double power = 2.5 + level;

            plr.getWorld().getNearbyEntities(playerLocation, radius, radius, radius)
                    .stream()
                    .filter(entity -> entity instanceof LivingEntity && entity.getUniqueId() != plr.getUniqueId())
                    .forEach(entity -> {
                        entity.setVelocity(entity.getVelocity().add(new Vector(0, power, 0)));
                        if (entity instanceof Player player) {
                            player.damage(power, plr);
                            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 1.0f);
                            if (PowerGems.isEnabled("CombatLogX") && gcm.isCombatLogXEnabled())
                                CombatLogXAddonManager.getInstance().setInFight(plr, player);
                        }
                    });
        });
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            double distance = 6 * (level / 2.0);
            Location location = plr.getLocation();
            Vector direction = location.getDirection().normalize();
            AreaEffectCloud effect = (AreaEffectCloud) plr.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
            effect.setDuration(60);
            effect.setRadius(1.0f);
            effect.setParticle(Particle.LARGE_SMOKE);
            effect.setColor(Color.BLACK);
            plr.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
            plr.setVelocity(direction.multiply(distance));
        });
    }

    @Override
    public ArrayList<String> getDefaultLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Component.text("Level %level%", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Abilities", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Right click: Creates a tether of wind between the player and a target player, pulling the target closer.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Shift click: Creates a cloud of smoke, granting temporary invisibility and propelling the player forward.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Left click: Unleashes a burst of wind, launching nearby entities into the air and dealing damage.", NamedTextColor.WHITE).toString());
        return lore;
    }

    @Override
    public PotionEffectType getDefaultEffectType() {
        return PotionEffectType.SLOW_FALLING;
    }

    @Override
    public int getDefaultEffectLevel() {
        return 1;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.CLOUD;
    }
}
