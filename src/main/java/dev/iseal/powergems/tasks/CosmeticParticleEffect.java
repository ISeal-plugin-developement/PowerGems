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
            gemManager.getPlayerGems(player).forEach(gem -> {
                Particle particle = gemManager.runParticleCall(gem, player);
                int level = gemManager.getLevel(gem);
                if (particle == null) return;
                for (int i = 0; i < level; i++) {
                    Location loc = utils.getRandomLocationCloseToPlayer(player);
                    player.getWorld().spawnParticle(particle, loc, 1, 0.001, 0.001, 0.001, 0.001);
                }
            });
        });
    }
}