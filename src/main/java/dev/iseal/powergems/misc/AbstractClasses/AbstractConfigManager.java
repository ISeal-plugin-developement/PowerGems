package dev.iseal.powergems.misc.AbstractClasses;

import de.leonhard.storage.Config;
import dev.iseal.powergems.managers.ConfigManager;
import dev.iseal.powergems.misc.Interfaces.Dumpable;

import java.util.HashMap;

public abstract class AbstractConfigManager implements Dumpable {

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

    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> map = new HashMap<>();
        file.keySet().forEach(key -> map.put(key, file.get(key)));
        return map;
    }

}
