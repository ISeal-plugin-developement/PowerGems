package dev.iseal.powergems.listeners.passivePowerListeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.gems.powerClasses.tasks.WaterRainingTask;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;

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
                .filter(Objects::nonNull)
                .filter(ItemStack::hasItemMeta) 
                .anyMatch(item -> {
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) {
                        return false;
                    }
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    String gemPower = container.get(nkm.getKey("gem_power"), PersistentDataType.STRING);
                    return gemPower != null && allowedGems.contains(gemPower);
                });
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
                .toList());

        task = new WaterRainingTask();
        task.runTaskTimer(PowerGems.getPlugin(), 0, 100);
    }

    @EventHandler(ignoreCancelled = true) // Add ignoreCancelled for better performance
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
