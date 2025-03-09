package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.sealLib.Utils.ExceptionHandler;

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
        if (file.contains(GemManager.lookUpName(i) + "EffectLevel")) return;
        if (i == -1) ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid gem ID: " + i), Level.WARNING, "CREATE_DEFAULT_LEVEL_SETTINGS");
        int setLevel = switch (i) {
            case 1 -> 1;
            case 2 -> 1;
            case 3 -> 1;
            case 4 -> 1;
            case 5 -> 1;
            case 6 -> 1;
            case 7 -> 1;
            case 8 -> 1;
            case 9 -> 1;
            case 10 -> 1;
            default -> 1;
        };
        file.set("Gem" + GemManager.lookUpName(i) + "EffectLevel", setLevel);
    }

    public int getLevel(int gemID) {
        return file.getInt("Gem" + gemID + "EffectLevel");
    }
}
