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

    }

    public void saveUpdatedCooldown() {
        long leftCooldown = 60L;
        long shiftCooldown = 50L;
        long rightCooldown = 40L;

        file.set("cooldown.leftClick", leftCooldown);
        file.set("cooldown.shiftClick", shiftCooldown);
        file.set("cooldown.rightClick", rightCooldown);

        // Use write() instead of save() to persist changes to disk
        file.write();
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
