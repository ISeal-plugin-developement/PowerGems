package dev.iseal.powergems.listeners.passivePowerListeners;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.GemSpecificListener;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class DebuffInHotBiomesListener extends GemSpecificListener {

    private final Utils utils = SingletonManager.getInstance().utils;

    public DebuffInHotBiomesListener() {
        super(List.of("Water", "Ice"), 60);
    }

    @Override
    public boolean applyEffect(PlayerMoveEvent e, int allowedGemNumber) {
        Player plr = e.getPlayer();
        if (plr.getLocation().getBlock().getTemperature() >= 0.95) {
            utils.addPreciseEffect(plr, PotionEffectType.SLOW, 60, allowedGemNumber);
            return true;
        }
        return false;
    }
}
