package dev.iseal.powergems.listeners.passivePowerListeners;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.GemSpecificListener;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class DebuffInColdBiomesListener extends GemSpecificListener {

    private final Utils utils = SingletonManager.getInstance().utils;

    public DebuffInColdBiomesListener() {
        super(List.of("Fire", "Lava"), 60);
    }

    @Override
    public boolean applyEffect(PlayerMoveEvent e, int allowedGemNumber) {
        Player plr = e.getPlayer();
        if (plr.getLocation().getBlock().getTemperature() <= 0.05) {
            utils.addPreciseEffect(plr, PotionEffectType.SLOW, 60, allowedGemNumber);
            return true;
        }
        return false;
    }
}