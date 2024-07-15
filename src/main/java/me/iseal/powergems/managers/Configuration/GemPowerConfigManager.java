package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import me.iseal.powergems.Main;
import me.iseal.powergems.managers.ConfigManager;

public class GemPowerConfigManager extends AbstractConfigManager {


    @Override
    public void setUpConfig() {
        file = new Config("GemPowerMultipliers", ConfigManager.getConfigFolderPath());
    }
}
