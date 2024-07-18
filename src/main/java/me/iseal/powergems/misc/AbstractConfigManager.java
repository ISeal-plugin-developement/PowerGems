package me.iseal.powergems.misc;

import de.leonhard.storage.Config;
import me.iseal.powergems.managers.SingletonManager;

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
