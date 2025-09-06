package dev.iseal.powergems.managers.Addons.CombatLogX;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ICombatLogXAddonImpl implements ICombatLogXAddon {
    private static ICombatLogXAddonImpl INSTANCE;
    public static ICombatLogXAddonImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ICombatLogXAddonImpl();
        }
        return INSTANCE;
    }
    private ICombatLogXAddonImpl() {
        // Private constructor to prevent instantiation
    }

    ICombatLogX plugin;

    public void init() {

    }

    private ICombatLogX getAPI() {
        if (plugin == null) {
            plugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
        }
        return plugin;
    }

    public void setInFightAttacker(Player player) {
        ICombatManager combatManager = getAPI().getCombatManager();
        combatManager.tag(player, null, TagType.UNKNOWN, TagReason.ATTACKER);
    }

    public void setInFight(Player attacker, Player defender) {
        ICombatManager combatManager = getAPI().getCombatManager();
        combatManager.tag(attacker, defender, TagType.UNKNOWN, TagReason.ATTACKER);
        combatManager.tag(defender, attacker, TagType.UNKNOWN, TagReason.ATTACKED);
    }
}
