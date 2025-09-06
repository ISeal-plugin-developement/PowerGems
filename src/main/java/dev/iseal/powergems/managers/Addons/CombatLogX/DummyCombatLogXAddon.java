package dev.iseal.powergems.managers.Addons.CombatLogX;

import dev.iseal.powergems.PowerGems;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class DummyCombatLogXAddon implements ICombatLogXAddon {
    private final Logger log = PowerGems.getPlugin().getLogger();

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
}
