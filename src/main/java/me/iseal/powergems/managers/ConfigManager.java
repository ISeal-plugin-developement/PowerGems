package me.iseal.powergems.managers;

import me.iseal.powergems.managers.Configuration.ActiveGemsConfigManager;
import me.iseal.powergems.managers.Configuration.CooldownConfigManager;
import me.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import me.iseal.powergems.managers.Configuration.GeneralConfigManager;

public class ConfigManager {

    private final GeneralConfigManager generalConfigManager = new GeneralConfigManager();
    private final ActiveGemsConfigManager activeGemsConfigManager = new ActiveGemsConfigManager();
    private final CooldownConfigManager cooldownConfigManager = new CooldownConfigManager();
    private final GemMaterialConfigManager gemMaterialConfigManager = new GemMaterialConfigManager();

    public void setUpConfig() {
        generalConfigManager.setUpConfig();
        activeGemsConfigManager.setUpConfig();
        cooldownConfigManager.setUpConfig();
        gemMaterialConfigManager.setUpConfig();
    }

    public GeneralConfigManager getGeneralConfigManager() {
        return generalConfigManager;
    }

    public ActiveGemsConfigManager getActiveGemsConfigManager() {
        return activeGemsConfigManager;
    }

    public CooldownConfigManager getCooldownConfigManager() {
        return cooldownConfigManager;
    }

    public GemMaterialConfigManager getGemMaterialConfigManager() {
        return gemMaterialConfigManager;
    }

    public void resetConfig() {
        generalConfigManager.resetConfig();
        activeGemsConfigManager.resetConfig();
        cooldownConfigManager.resetConfig();
        gemMaterialConfigManager.resetConfig();
    }

}
