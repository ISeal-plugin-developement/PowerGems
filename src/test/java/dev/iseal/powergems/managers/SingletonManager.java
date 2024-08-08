package dev.iseal.powergems.managers;

import dev.iseal.powergems.listeners.FallingBlockHitListener;
import dev.iseal.powergems.listeners.powerListeners.IceTargetListener;
import dev.iseal.powergems.listeners.powerListeners.SandMoveListener;
import dev.iseal.powergems.listeners.powerListeners.StrenghtMoveListener;
import dev.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.Metrics.MetricsManager;
import dev.iseal.powergems.misc.Utils;

public class SingletonManager {

    private static SingletonManager instance;

    public static SingletonManager getInstance() {
        if (instance == null) {
            instance = new SingletonManager();
        }
        return instance;
    }

    // Classes
    public GemManager gemManager;
    public StrenghtMoveListener strenghtMoveListen;
    public Utils utils;
    public RecipeManager recipeManager;
    public UpdaterManager updaterManager;
    public ConfigManager configManager;
    public CooldownManager cooldownManager;
    public SandMoveListener sandMoveListen;
    public IceTargetListener iceTargetListen;
    public FallingBlockHitListener fallingBlockHitListen;
    public TempDataManager tempDataManager;
    public NamespacedKeyManager namespacedKeyManager;
    public dev.iseal.powergems.managers.Metrics.MetricsManager metricsManager;

    public void init() {
        configManager = new ConfigManager();
        configManager.setUpConfig();
        namespacedKeyManager = new NamespacedKeyManager();
        updaterManager = new UpdaterManager();
        tempDataManager = new TempDataManager();
        sandMoveListen = new SandMoveListener();
        iceTargetListen = new IceTargetListener();
        fallingBlockHitListen = new FallingBlockHitListener();
        gemManager = new GemManager();
        strenghtMoveListen = new StrenghtMoveListener();
        utils = new Utils();
        recipeManager = new RecipeManager();
        cooldownManager = new CooldownManager();
        metricsManager = new MetricsManager();
    }

    public void initLater() {
        gemManager.initLater();
        configManager.getRegisteredConfigInstance(GemMaterialConfigManager.class).lateInit();
        if (configManager.getRegisteredConfigInstance(GeneralConfigManager.class).canRunUpdater())
            updaterManager.start();
        recipeManager.initiateRecipes();
    }
}
