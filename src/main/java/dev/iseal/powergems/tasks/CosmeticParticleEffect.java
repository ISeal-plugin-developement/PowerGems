package dev.iseal.powergems.tasks;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class CosmeticParticleEffect extends BukkitRunnable {

    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            utils.getUserGems(player).forEach(gem -> {
                Particle particle = gemManager.runParticleCall(gem, player);
                if (particle == null) return;
                Location loc = utils.getRandomLocationCloseToPlayer(player);
                player.getWorld().spawnParticle(particle, loc, 1);
            });
        });
    }
}