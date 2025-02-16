package dev.iseal.powergems.tasks;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.Utils;

public class CheckMultipleEmeraldsTask implements Runnable {

    private final Utils ut = SingletonManager.getInstance().utils;
    private final GemManager dm = SingletonManager.getInstance().gemManager;
    private final Random random = new Random();

    @Override
    public void run() {
        ArrayList<ItemStack> gems = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (ut.hasAtLeastXAmountOfGems(player, 2)) {
                player.getInventory().all(Material.EMERALD).values().stream()
                    .filter(dm::isGem)
                    .peek(item -> item.setAmount(1))
                    .forEach(gems::add);

                // Check offhand
                ItemStack offhand = player.getInventory().getItemInOffHand();
                if (dm.isGem(offhand)) {
                    offhand.setAmount(1);
                    gems.add(offhand);
                }

                // Remove extra gems
                if (gems.size() > 1) {
                    while (gems.size() > 1) {
                        ItemStack gem = gems.get(random.nextInt(gems.size()));
                        player.getInventory().removeItem(gem);
                        gems.remove(gem);
                    }
                }
                gems.clear();
            }
        });
    }
    public static void schedule(PowerGems plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            new CheckMultipleEmeraldsTask().run();
        }, 100L, 60L);
    }
}