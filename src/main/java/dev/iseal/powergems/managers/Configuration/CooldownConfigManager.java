package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class CooldownConfigManager extends AbstractConfigManager {

    public CooldownConfigManager() {
        super("cooldowns");
    }

    @Override
    public void setUpConfig() {

    }

    public int getStartingCooldown(String name, String ability) {
        return file.getOrSetDefault(name + ability + "Cooldown", 60);
    }

}
