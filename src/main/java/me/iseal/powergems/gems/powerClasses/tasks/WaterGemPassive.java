package me.iseal.powergems.gems.powerClasses.tasks;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.GemManager;
import me.iseal.powergems.managers.SingletonManager;
import me.iseal.powergems.misc.Utils;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class WaterGemPassive extends BukkitRunnable {

    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    @Override
    public void run() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            utils.getUserGems(player).forEach(gem -> {
                if (gemManager.getGemName(gem).equalsIgnoreCase("Water")) {
                    if (player.getLocation().getBlock().isLiquid()) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20, 0));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 0));
                    }
                }
            });

        });
    }
}
