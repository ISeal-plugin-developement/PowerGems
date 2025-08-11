package dev.iseal.powergems.gems;

import dev.iseal.powergems.listeners.AvoidTargetListener;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class LavaGem extends Gem {

    private final Utils u = SingletonManager.getInstance().utils;
    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    public LavaGem() {
        super("Lava");
    }

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            int radius = 5;
            int times = level / 2 + 1;

            for (int currentTime = 0; currentTime < times; currentTime++) {
                final int finalRadius = radius;
                final int finalCurrentTime = currentTime;

                // Schedule each lava ring creation at the correct location
                schedulerWrapper.scheduleDelayedTaskAtLocation(plr.getLocation(), () -> {
                    ArrayList<Block> blocks = u.getSquareOutlineAirBlocks(plr, finalRadius);
                    blocks.forEach(block -> block.setType(Material.LAVA));

                    // Schedule cleanup in the same region
                    schedulerWrapper.scheduleDelayedTaskAtLocation(plr.getLocation(), () ->
                            blocks.forEach(block -> block.setType(Material.AIR)
                            ), 600 + (finalCurrentTime * 20L));
                }, finalCurrentTime * 20L);

                radius += 3;
            }
        });
    }

    @Override
    protected void leftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0));
        });
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        schedulerWrapper.scheduleTaskForEntity(plr, () -> {
            LivingEntity blaze = (LivingEntity) plr.getWorld().spawnEntity(plr.getLocation(), EntityType.BLAZE);
            blaze.customName(Component.text(I18N.translate("OWNED_BLAZE").replace("{owner}", plr.getName())));
            blaze.setCustomNameVisible(true);
            blaze.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 1200, level - 1));
            AvoidTargetListener.getInstance().addToList(plr, blaze, 1200);
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
        lore.add(Component.text("Right click: Make a wall of lava", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Shift click: Spawn a blaze to fight for you", NamedTextColor.WHITE).toString());
        lore.add(Component.text("Left click: 1 minute of Fire resistance", NamedTextColor.WHITE).toString());
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.LAVA;
    }
}
