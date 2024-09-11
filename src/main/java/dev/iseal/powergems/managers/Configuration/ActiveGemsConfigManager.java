package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class ActiveGemsConfigManager extends AbstractConfigManager {

    public ActiveGemsConfigManager() {
        super("activeGems");
    }

    @Override
    public void setUpConfig() {

    }

    public boolean isGemActive(String name) {
        return file.getOrSetDefault(name + "GemActive", true);
    }

}
