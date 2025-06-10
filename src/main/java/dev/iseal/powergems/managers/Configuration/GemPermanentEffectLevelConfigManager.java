package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.sealUtils.utils.ExceptionHandler;

import java.util.logging.Level;

public class GemPermanentEffectLevelConfigManager extends AbstractConfigManager {
    public GemPermanentEffectLevelConfigManager() {
        super("GemPermanentEffectLevels");
    }

    @Override
    public void setUpConfig() {

    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            createDefaultLevelSettings(i);
        }
    }

    private void createDefaultLevelSettings(int i) {
        if (file.contains(GemManager.lookUpName(i) + "GemEffectLevel")) return;
        if (i == -1) ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid gem ID: " + i), Level.WARNING, "CREATE_DEFAULT_LEVEL_SETTINGS");
        file.setDefault(
                GemManager.lookUpName(i) + "GemEffectLevel",
                GemReflectionManager.getInstance().getSingletonGemInstance(GemManager.lookUpName(i)).getDefaultEffectLevel()
        );
    }

    public int getLevel(String gemName) {
        return file.getInt(gemName + "GemEffectLevel");
    }
}
