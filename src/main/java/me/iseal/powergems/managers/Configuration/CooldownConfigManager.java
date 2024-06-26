package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import de.leonhard.storage.Yaml;
import me.iseal.powergems.Main;

public class CooldownConfigManager extends AbstractConfigManager {

    @Override
    public void setUpConfig() {
        file = new Config("cooldowns", Main.getPlugin().getDataFolder() + "\\config\\");
    }

    public int getStartingCooldown(String name, String ability) {
        return file.getOrSetDefault(name + ability + "Cooldown", 60);
    }

}
