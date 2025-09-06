package dev.iseal.powergems.managers.Addons;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Addons.CombatLogX.ICombatLogXAddonImpl;
import dev.iseal.powergems.managers.Addons.CombatLogX.DummyCombatLogXAddon;
import dev.iseal.powergems.managers.Addons.CombatLogX.ICombatLogXAddon;
import dev.iseal.powergems.managers.Addons.WorldGuard.WorldGuardAddonManager;
import dev.iseal.powergems.managers.ConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import org.bukkit.Bukkit;

public class AddonsManager {

    public static final AddonsManager INSTANCE = new AddonsManager();

    private AddonsManager() {}

    private final GeneralConfigManager gcm = ConfigManager.getInstance().getRegisteredConfigInstance(GeneralConfigManager.class);

    public void loadAddons() {
        if (PowerGems.isEnabled("CombatLogX") && gcm.isCombatLogXEnabled()) {
            ICombatLogXAddon addon;
            if (isJava21OrNewer()) {
                try {
                    // This is the only place with a direct reference to the real manager
                    addon = ICombatLogXAddonImpl.getInstance();
                    Bukkit.getLogger().info("Successfully loaded CombatLogX addon.");
                } catch (Throwable t) {
                    Bukkit.getLogger().warning("Failed to load CombatLogX addon, likely due to a version mismatch.");
                    addon = new DummyCombatLogXAddon();
                }
            } else {
                addon = new DummyCombatLogXAddon();
            }
            addon.init();
        }

        if (PowerGems.isEnabled("WorldGuard") && gcm.isWorldGuardEnabled()) {
            WorldGuardAddonManager.getInstance().init();
        }
    }

    private boolean isJava21OrNewer() {
        // A simple way to check the Java version
        String version = System.getProperty("java.version");
        try {
            int major = Integer.parseInt(version.split("\\.")[0]);
            return major >= 21;
        } catch (NumberFormatException e) {
            // Handle versions like "1.8.0_292"
            if (version.startsWith("1.")) {
                return false; // Java 8 is not >= 21
            }
        }
        return false;
    }
}
