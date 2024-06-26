package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;

public abstract class AbstractConfigManager {

    protected Config file;

    public abstract void setUpConfig();

    public void reloadConfig() {
        file.forceReload();
    }

    public void resetConfig() {
        file.clear();
        setUpConfig();
    }

}
