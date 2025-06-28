package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class ActiveGemsConfigManager extends AbstractConfigManager {

    public ActiveGemsConfigManager() {
        super("activeGems");
    }

    @Override
    public void setUpConfig() {
    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            file.setDefault(GemManager.lookUpName(i) + "GemActive", true);
        }
    }

    public boolean isGemActive(String name) {
        return file.getOrSetDefault(name + "GemActive", true);
    }

}
