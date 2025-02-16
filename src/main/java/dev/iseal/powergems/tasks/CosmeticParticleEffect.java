package dev.iseal.powergems.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.misc.Utils;

public class CosmeticParticleEffect implements Runnable {

    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            utils.getUserGems(player).forEach(gem -> {
                Particle particle = gemManager.runParticleCall(gem, player);
                int level = gemManager.getLevel(gem);
                if (particle == null) return;

                World world = player.getWorld();

                for (int i = 0; i < level; i++) {
                    Location particleLoc = utils.getRandomLocationCloseToPlayer(player);
                    world.spawnParticle(
                        particle,
                        particleLoc,
                        1,
                        0.001,
                        0.001,
                        0.001,
                        0.001,
                        null,
                        true
                    );
                }
            });
        });
    }

    public static void schedule(PowerGems plugin) {
        GeneralConfigManager gcm = SingletonManager.getInstance().configManager
                .getRegisteredConfigInstance(GeneralConfigManager.class);
        plugin.getServer().getScheduler().runTaskTimer(
            plugin,
            new CosmeticParticleEffect(),
            0L,
            gcm.cosmeticParticleEffectInterval()
        );
    }
}