package dev.iseal.powergems.managers.Addons;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Addons.CombatLogX.ICombatLogXAddon;
import dev.iseal.powergems.managers.Addons.CombatLogX.ICombatLogXAddonImpl;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddonsManager {

    public static final AddonsManager INSTANCE = new AddonsManager();

    private AddonsManager() {}

    private final Map<String, AbstractAddon> activeAddons = new HashMap<>();
    private final Map<String, Boolean> loadedAddons = new HashMap<>();

    public void initAddons(AddonLoadOrder loadOrder) {
        Arrays.stream(Addon.values())
                .map(Addon::getAddon)
                .filter(addon -> addon.getLoadOrder() == loadOrder)
                .forEach(this::tryInitAddon);
    }

    private void tryInitAddon(AbstractAddon addon) {
        String addonName = addon.getClass().getSimpleName();
        boolean shouldLoad = true;

        if (addon instanceof ICombatLogXAddon) {
            if (!PowerGems.isEnabled(addon.getPluginName()) || !addon.isEnabledInConfig()) {
                shouldLoad = false;
            } else if (addon instanceof ICombatLogXAddonImpl && !isJava21OrNewer()) {
                Bukkit.getLogger().warning("CombatLogX addon requires Java 21 or newer. Running in dummy mode.");
                loadedAddons.put("CombatLogX", false);
                AbstractAddon dummy = Addon.DUMMY_COMBATLOGX.getAddon();
                dummy.init();
                activeAddons.put(ICombatLogXAddon.class.getSimpleName(), dummy);
                return;
            }
        } else {
            String pluginName = addon.getPluginName();
            if (pluginName != null && (!PowerGems.isEnabled(pluginName) || !addon.isEnabledInConfig())) {
                shouldLoad = false;
            }
        }

        if (shouldLoad) {
            try {
                addon.init();
                String key = addonName.replace("Impl", "");
                if (addon instanceof ICombatLogXAddon) key = ICombatLogXAddon.class.getSimpleName();
                activeAddons.put(key, addon);
                loadedAddons.put(key.replace("Addon", ""), true);
                Bukkit.getLogger().info("Successfully loaded " + addonName + " addon.");
            } catch (Throwable t) {
                Bukkit.getLogger().warning("Failed to load " + addonName + " addon, likely due to a version mismatch.");
                loadedAddons.put(addonName.replace("Addon", "").replace("Impl", ""), false);
            }
        }
    }

    private boolean isJava21OrNewer() {
        // A simple way to check the Java version
        String version = System.getProperty("java.version");
        try {
            int major = Integer.parseInt(version.split("\\.")[0]);
            if (major == 1) { // Handle "1.8", "1.11", etc.
                major = Integer.parseInt(version.split("\\.")[1]);
                return major >= 21;
            }
            return major >= 21;
        } catch (NumberFormatException e) {
            // Handle versions like "1.8.0_292"
            if (version.startsWith("1.")) {
                return false; // Java 8 is not >= 21
            }
        }
        return false;
    }

    public boolean isAddonLoaded(String addonName) {
        return loadedAddons.getOrDefault(addonName, false);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractAddon> T getAddon(Class<T> addonClass) {
        return (T) activeAddons.get(addonClass.getSimpleName());
    }
}
