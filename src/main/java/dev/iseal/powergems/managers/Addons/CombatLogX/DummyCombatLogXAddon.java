package dev.iseal.powergems.managers.Addons.CombatLogX;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Addons.AbstractAddon;
import dev.iseal.powergems.managers.Addons.AddonLoadOrder;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class DummyCombatLogXAddon extends AbstractAddon implements ICombatLogXAddon {
    private static DummyCombatLogXAddon INSTANCE;
    public static DummyCombatLogXAddon getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DummyCombatLogXAddon();
        }
        return INSTANCE;
    }

    private final Logger log = PowerGems.getPlugin().getLogger();

    private DummyCombatLogXAddon() {
        super(AddonLoadOrder.AFTER_SERVER_LOAD);
    }

    @Override
    public void init() {
        log.info("Dummy CombatLogX addon initialized. Java version requirement not met.");
    }

    @Override
    public void setInFightAttacker(Player player) {
        log.info("Dummy CombatLogX addon setInFightAttacker called.");
    }

    @Override
    public void setInFight(Player attacker, Player defender) {
        log.info("Dummy CombatLogX addon setInFight called.");
    }

    @Override
    public String getPluginName() {
        return "CombatLogX";
    }

    @Override
    public boolean isEnabledInConfig() {
        return gcm.isCombatLogXEnabled();
    }
}
