package me.iseal.powergems.gems;

import me.iseal.powergems.Main;
import me.iseal.powergems.listeners.powerListeners.lavaTargetListener;
import me.iseal.powergems.misc.Gem;
import me.iseal.powergems.misc.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class LavaGem extends Gem {

    private final lavaTargetListener ltl = new lavaTargetListener();
    private final Utils u = Main.getSingletonManager().utils;

    @Override
    public void call(Action act, Player plr, ItemStack item) {
        caller = this.getClass();
        super.call(act, plr, item);
    }

    @Override
    protected void rightClick(Player plr) {
        int radius = 5;
        int times = (level / 2) + 1;

        while (times != 0) {
            ArrayList<Block> blocks = u.getSquareOutlineAirBlocks(plr, radius);
            // Set the blocks to lava
            blocks.forEach(nullBlock -> {
                nullBlock.setType(Material.LAVA);
            });

            // Set the blocks to air
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    blocks.forEach(nullBlock -> {
                        nullBlock.setType(Material.AIR);
                    });
                }
            }, 600 + (times * 20));
            times--;
            radius = radius + 3;
        }
    }

    @Override
    protected void leftClick(Player plr) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0));
    }

    @Override
    protected void shiftClick(Player plr) {
        plr.getWorld().spawnEntity(plr.getLocation(), EntityType.BLAZE);
        ltl.addToList(plr);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ltl.removeFromList(plr);
            }
        }, 2400L);
    }
}
