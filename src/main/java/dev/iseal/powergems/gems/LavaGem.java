package dev.iseal.powergems.gems;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.listeners.AvoidTargetListener;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class LavaGem extends Gem {

    public LavaGem() {
        super("Lava");
    }

    private final Utils u = SingletonManager.getInstance().utils;

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr, int level) {
        int radius = 5;
        int times = level / 2 + 1;

        while (times != 0) {
            ArrayList<Block> blocks = u.getSquareOutlineAirBlocks(plr, radius);
            // Set the blocks to lava
            blocks.forEach(nullBlock -> {
                nullBlock.setType(Material.LAVA);
            });

            // Set the blocks to air
            Bukkit.getScheduler().scheduleSyncDelayedTask(PowerGems.getPlugin(), () ->
                    blocks.forEach(nullBlock ->
                            nullBlock.setType(Material.AIR)
                    ), 600 + (times * 20L));
            times--;
            radius = radius + 3;
        }
    }

    @Override
    protected void leftClick(Player plr, int level) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0));
    }

    @Override
    protected void shiftClick(Player plr, int level) {
        for (int i = 0; i < level; i++) {
            LivingEntity blaze = (LivingEntity) plr.getWorld().spawnEntity(plr.getLocation(), EntityType.BLAZE);
            blaze.setCustomName(I18N.translate("OWNED_BLAZE").replace("{owner}", plr.getName()));
            blaze.setCustomNameVisible(true);
            AvoidTargetListener.getInstance().addToList(plr, blaze);
            blaze.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, PotionEffect.INFINITE_DURATION, level - 1));
        }
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
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        lore.add(ChatColor.WHITE + "Right click: Make a wall of lava");
        lore.add(ChatColor.WHITE + "Shift click: Spawn a blaze to fight for you");
        lore.add(ChatColor.WHITE + "Left click: 1 minute of Fire resistance");
        return lore;
    }

    @Override
    public Particle getDefaultParticle() {
        return Particle.LAVA;
    }

    @Override
    public BlockData getParticleBlockData() {
        return null;
    }
}
