package dev.iseal.powergems.managers.Addons;

import dev.iseal.powergems.managers.ConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;

public abstract class AbstractAddon {

    private final AddonLoadOrder loadOrder;
    protected final GeneralConfigManager gcm = ConfigManager.getInstance().getRegisteredConfigInstance(GeneralConfigManager.class);

    protected AbstractAddon(AddonLoadOrder loadOrder) {
        this.loadOrder = loadOrder;
    }

    public AddonLoadOrder getLoadOrder() {
        return loadOrder;
    }

    public abstract void init();

    public String getPluginName() {
        return null;
    }

    public boolean isEnabledInConfig() {
        return true;
    }

}
