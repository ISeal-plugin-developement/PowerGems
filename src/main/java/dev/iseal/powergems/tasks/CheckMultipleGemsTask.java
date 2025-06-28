package dev.iseal.powergems.tasks;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class CheckMultipleGemsTask extends BukkitRunnable {

    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;

    @Override
    public void run() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            ArrayList<ItemStack> gems = gemManager.getPlayerGems(player);
            if (gems.size() > 1) {
                gems.sort((o1, o2) -> {
                    long t1 = gemManager.getGemCreationTime(o1);
                    long t2 = gemManager.getGemCreationTime(o2);
                    return Long.compare(t2, t1); // Newest first
                });
                // Keep the first (newest), remove the rest
                for (int i = 1; i < gems.size(); i++) {
                    player.getInventory().remove(gems.get(i));
                }
            }
        });
    }
}