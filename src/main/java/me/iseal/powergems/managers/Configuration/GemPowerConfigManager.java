package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import me.iseal.powergems.managers.ConfigManager;
import me.iseal.powergems.misc.AbstractConfigManager;

public class GemPowerConfigManager extends AbstractConfigManager {


    @Override
    public void setUpConfig() {
        file = new Config("GemPowerMultipliers", ConfigManager.getConfigFolderPath());
    }
}
