package dev.iseal.powergems.misc.AbstractClasses;

import de.leonhard.storage.Config;
import dev.iseal.powergems.managers.ConfigManager;

public abstract class AbstractConfigManager {

    protected Config file;

    public AbstractConfigManager(String name) {
        if (name != null)
            this.file = new Config(name, ConfigManager.getConfigFolderPath());
    }

    public abstract void setUpConfig();

    public void reloadConfig() {
        file.forceReload();
    }

    public void resetConfig() {
        file.clear();
        setUpConfig();
    }

    public abstract void lateInit();

}
