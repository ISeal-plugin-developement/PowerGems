package dev.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class ActiveGemsConfigManager extends AbstractConfigManager {

    @Override
    public void setUpConfig() {
        file = new Config("activeGems", PowerGems.getPlugin().getDataFolder() + "\\config\\");
    }

    public boolean isGemActive(String name) {
        return file.getOrSetDefault(name + "GemActive", true);
    }

}
