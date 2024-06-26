package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import de.leonhard.storage.Yaml;
import me.iseal.powergems.Main;

public class ActiveGemsConfigManager extends AbstractConfigManager {

    @Override
    public void setUpConfig() {
        file = new Config("activeGems", Main.getPlugin().getDataFolder() + "\\config\\");
    }

    public boolean isGemActive(String name) {
        return file.getOrSetDefault(name + "GemActive", true);
    }

}
