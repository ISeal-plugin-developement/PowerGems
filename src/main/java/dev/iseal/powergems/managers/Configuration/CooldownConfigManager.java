package dev.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.misc.AbstractConfigManager;

public class CooldownConfigManager extends AbstractConfigManager {

    @Override
    public void setUpConfig() {
        file = new Config("cooldowns", PowerGems.getPlugin().getDataFolder() + "\\config\\");
    }

    public int getStartingCooldown(String name, String ability) {
        return file.getOrSetDefault(name + ability + "Cooldown", 60);
    }

}
