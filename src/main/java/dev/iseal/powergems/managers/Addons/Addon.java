package dev.iseal.powergems.managers.Addons;

import dev.iseal.powergems.managers.Addons.CombatLogX.DummyCombatLogXAddon;
import dev.iseal.powergems.managers.Addons.CombatLogX.ICombatLogXAddon;
import dev.iseal.powergems.managers.Addons.CombatLogX.ICombatLogXAddonImpl;
import dev.iseal.powergems.managers.Addons.WorldGuard.WorldGuardAddon;

public enum Addon {

    DUMMY_COMBATLOGX(DummyCombatLogXAddon.class),
    COMBATLOGX(ICombatLogXAddonImpl.class),
    WORLD_GUARD(WorldGuardAddon.class);

    private final Class<? extends AbstractAddon> addonClass;

    Addon(Class<? extends AbstractAddon> addonClass) {
        this.addonClass = addonClass;
    }

    public AbstractAddon getAddon() {
        try {
            return (AbstractAddon) addonClass.getMethod("getInstance").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get addon instance for " + name(), e);
        }
    }
}
