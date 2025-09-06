package dev.iseal.powergems.managers.Addons.CombatLogX;

import org.bukkit.entity.Player;

public interface ICombatLogXAddon {
    void init();
    void setInFightAttacker(Player player);
    void setInFight(Player attacker, Player defender);
}
