package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class CooldownConfigManager extends AbstractConfigManager {

    public CooldownConfigManager() {
        super("cooldowns");
    }

    @Override
    public void setUpConfig() {
        file.setHeader(
                "This config file manages the cooldowns of the gems",
                "The cooldowns are in seconds",
                "The default cooldown is 60 seconds",
                "Change the numbers to whatever you want change the cooldowns"
        );

    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            file.setDefault(GemManager.lookUpName(i) + "LeftCooldown", 60);
            file.setDefault(GemManager.lookUpName(i) + "RightCooldown", 60);
            file.setDefault(GemManager.lookUpName(i) + "ShiftCooldown", 60);
        }
    }

    public int getStartingCooldown(String name, String ability) {
        return file.getOrSetDefault(name + ability + "Cooldown", 60);
    }

}
