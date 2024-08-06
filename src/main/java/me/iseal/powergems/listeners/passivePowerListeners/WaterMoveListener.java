package me.iseal.powergems.listeners.passivePowerListeners;

import me.iseal.powergems.Main;
import me.iseal.powergems.gems.powerClasses.tasks.WaterRainingTask;
import me.iseal.powergems.managers.GemManager;
import me.iseal.powergems.managers.NamespacedKeyManager;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WaterMoveListener implements Listener {

    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    private final List<String> allowedGems = List.of("Water");
    private final List<UUID> swimmingPlayers = new ArrayList<>();
    public static final ArrayList<UUID> hasGemRaining = new ArrayList<>();
    private WaterRainingTask task;
    private boolean isRaining = false;

    private boolean playerHasAllowedGem(Player plr) {
        return gm.getPlayerGems(plr).stream()
                .anyMatch(i -> allowedGems.contains(i.getItemMeta().getPersistentDataContainer().get(nkm.getKey("gem_power"), PersistentDataType.STRING)));
    }

    private void applyPotionEffects(Player plr, int duration, int amplifier) {
        plr.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, amplifier));
        plr.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, amplifier));
        plr.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!event.toWeatherState()) {
            isRaining = false;
            hasGemRaining.clear();
            if (task != null) task.cancel();
            return;
        }
        isRaining = true;

        hasGemRaining.addAll(Bukkit.getOnlinePlayers().stream()
                .filter(this::playerHasAllowedGem)
                .map(Player::getUniqueId)
                .collect(Collectors.toList()));

        task = new WaterRainingTask();
        task.runTaskTimer(Main.getPlugin(), 0, 100);
    }

    @EventHandler
    public void onSwim(EntityToggleSwimEvent e) {
        if (!(e.getEntity() instanceof Player plr)) return;
        if (!e.isSwimming()) {
            swimmingPlayers.remove(plr.getUniqueId());
            return;
        }
        if (playerHasAllowedGem(plr)) {
            applyPotionEffects(plr, 100, 0);
            swimmingPlayers.add(plr.getUniqueId());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        swimmingPlayers.remove(e.getPlayer().getUniqueId());
        hasGemRaining.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (isRaining && playerHasAllowedGem(e.getPlayer())) {
            hasGemRaining.add(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player plr = e.getPlayer();
        if (swimmingPlayers.contains(plr.getUniqueId())) {
            applyPotionEffects(plr, 100, 0);
        }
    }

}
