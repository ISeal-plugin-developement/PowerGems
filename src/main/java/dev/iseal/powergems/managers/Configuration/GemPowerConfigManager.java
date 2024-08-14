package dev.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import dev.iseal.powergems.managers.ConfigManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class GemPowerConfigManager extends AbstractConfigManager {


    @Override
    public void setUpConfig() {
        file = new Config("GemPowerMultipliers", ConfigManager.getConfigFolderPath());
    }
}
