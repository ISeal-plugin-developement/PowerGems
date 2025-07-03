package dev.iseal.powergems.tasks;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;


public class CosmeticParticleEffect extends BukkitRunnable {

    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    /**
     * Spawns cosmetic particle effects for gems.
     */
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            schedulerWrapper.runTaskForEntity(player, () -> {
                ArrayList<ItemStack> gems = gemManager.getPlayerGems(player);
                for (ItemStack gem : gems) {
                    Particle particle = gemManager.runParticleCall(gem, player);
                    if (particle == null) continue;

                    int level = gemManager.getLevel(gem);
                    for (int i = 0; i < level; i++) {
                        Location loc = utils.getRandomLocationCloseToPlayer(player);
                        player.getWorld().spawnParticle(particle, loc, 1, 0.001, 0.001, 0.001, 0.001);
                    }
                }
            });
        }
    }
}