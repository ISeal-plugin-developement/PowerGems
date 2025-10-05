package dev.iseal.powergems.managers.Addons;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Addons.CombatLogX.DummyCombatLogXAddon;
import dev.iseal.powergems.managers.Addons.CombatLogX.ICombatLogXAddonImpl;
import dev.iseal.powergems.managers.Addons.WorldGuard.WorldGuardAddon;

public enum Addon {

    DUMMY_COMBATLOGX(DummyCombatLogXAddon.class, "CombatLogX"),
    COMBATLOGX(ICombatLogXAddonImpl.class, "CombatLogX"),
    WORLD_GUARD(WorldGuardAddon.class, "WorldGuard");

    private final Class<? extends AbstractAddon> addonClass;
    private final String[] requiredPlugins;

    Addon(Class<? extends AbstractAddon> addonClass, String... requiredPlugins) {
        this.addonClass = addonClass;
        this.requiredPlugins = requiredPlugins;
    }

    public AbstractAddon getAddon() {
        try {
            return (AbstractAddon) addonClass.getMethod("getInstance").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get addon instance for " + name(), e);
        }
    }

    public boolean hasRequiredPlugins() {
        for (String plugin : requiredPlugins) {
            if (!plugin.isEmpty() && !PowerGems.isEnabled(plugin)) {
                return false;
            }
        }
        return true;
    }
}
