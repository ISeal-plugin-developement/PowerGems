package dev.iseal.powergems.gems;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

import java.util.ArrayList;

public class LightningGem extends Gem {
    public LightningGem() {
        super("Lightning");
    }

    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Block possibleTarget = plr.getTargetBlock(null, 90);
            Location targetLocation = possibleTarget.getLocation();
            World plrWorld = plr.getWorld();

            schedulerWrapper.scheduleDelayedTaskAtLocation(targetLocation, () -> {
                plrWorld.strikeLightning(targetLocation);

                for (Entity e : plrWorld.getNearbyEntities(targetLocation, 5, 5, 5)) {
                    if (e instanceof LivingEntity) {
                        plrWorld.strikeLightning(e.getLocation());
                    }
                }
            }, 1L);
        });
    }

    @Override
    protected void leftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location playerLocation = plr.getLocation();
            World world = playerLocation.getWorld();
            plr.setVelocity(playerLocation.getDirection().multiply(5));
            world.spawnParticle(Particle.FLASH, playerLocation, 100, 0, 0, 0, 0.2);
        });
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            Location playerLocation = plr.getLocation();
            World world = playerLocation.getWorld();
            world.playSound(playerLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);

            for (Entity e : world.getNearbyEntities(playerLocation, 5, 5, 5)) {
                if (e instanceof LivingEntity && e != plr) {
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 0));
                }
            }
        });
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
        lore.add(Component.text("Level %level%", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Abilities", NamedTextColor.GREEN).toString());
        lore.add(Component.text("Right click: Strikes lightning at the target location and nearby entities, damaging them.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Shift click: Emits a thunder sound effect and applies a glowing potion effect to nearby entities, excluding the player.", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Left click: Launches the player forward in the direction rail.", NamedTextColor.WHITE).toString());
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.ELECTRIC_SPARK;
    }
}
