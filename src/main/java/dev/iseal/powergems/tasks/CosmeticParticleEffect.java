package dev.iseal.powergems.tasks;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

public class CosmeticParticleEffect extends BukkitRunnable {

    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final GemReflectionManager gemReflectionManager = GemReflectionManager.getInstance();

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            gemManager.getPlayerGems(player).forEach(gem -> {
                Gem gemInstance = gemReflectionManager.getGemInstance(gem, player);
                Particle particle = gemInstance.particle();
                BlockData blockData = gemInstance.getParticleBlockData();
                int level = gemManager.getLevel(gem);
                if (particle == null) return;
                for (int i = 0; i < level; i++) {
                    Location loc = utils.getRandomLocationCloseToPlayer(player);
                    if (blockData == null) {
                        player.getWorld().spawnParticle(particle, loc, 1, 0.001, 0.001, 0.001, 0.001);
                    } else {
                        player.getWorld().spawnParticle(particle, loc, 1, 0.001,0.001,0.001, 0.001, blockData);
                    }
                }
            });
        });
    }
}