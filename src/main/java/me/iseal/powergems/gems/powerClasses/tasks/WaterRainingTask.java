package me.iseal.powergems.gems.powerClasses.tasks;

import me.iseal.powergems.listeners.passivePowerListeners.WaterMoveListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class WaterRainingTask extends BukkitRunnable {

    private ArrayList<UUID> hasGemRaining = new ArrayList<>();

    @Override
    public void run() {
        this.hasGemRaining = WaterMoveListener.hasGemRaining;
        for (UUID uuid : hasGemRaining) {
            Player plr = Bukkit.getPlayer(uuid);
            double temperature = plr.getLocation().getBlock().getTemperature();
            boolean canSeeSky = plr.getWorld().getHighestBlockAt(plr.getLocation()).getY() <= plr.getLocation().getY();
            if (temperature > 0.15 && temperature < 0.95 && canSeeSky) {
                // it is raining here
                plr.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 150, 0));
            }
        }
    }
}
