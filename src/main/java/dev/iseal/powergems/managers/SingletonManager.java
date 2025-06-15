package dev.iseal.powergems.managers;

import dev.iseal.powergems.listeners.FallingBlockHitListener;
import dev.iseal.powergems.listeners.powerListeners.SandMoveListener;
import dev.iseal.powergems.listeners.powerListeners.StrenghtMoveListener;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.sealLib.Metrics.MetricsManager;

public class SingletonManager {

    public static int TOTAL_GEM_AMOUNT = 0;
    public static int gemCacheExpireTime;
    private static SingletonManager instance;

    public static SingletonManager getInstance() {
        if (instance == null) {
            instance = new SingletonManager();
        }
        return instance;
    }

    // Classes
    public GemManager gemManager;
    public StrenghtMoveListener strengthMoveListen;
    public Utils utils;
    public RecipeManager recipeManager;
    public UpdaterManager updaterManager;
    public ConfigManager configManager;
    public CooldownManager cooldownManager;
    public SandMoveListener sandMoveListen;
    public FallingBlockHitListener fallingBlockHitListen;
    public TempDataManager tempDataManager;
    public NamespacedKeyManager namespacedKeyManager;
    public MetricsManager metricsManager;

    public void init() {
        configManager = ConfigManager.getInstance();
        configManager.setUpConfig();
        namespacedKeyManager = new NamespacedKeyManager();
        updaterManager = UpdaterManager.getInstance();
        tempDataManager = new TempDataManager();
        sandMoveListen = new SandMoveListener();
        fallingBlockHitListen = new FallingBlockHitListener();
        gemManager = GemManager.getInstance();
        strengthMoveListen = new StrenghtMoveListener();
        utils = new Utils();
        recipeManager = RecipeManager.getInstance();
        cooldownManager = CooldownManager.getInstance();
        gemCacheExpireTime = configManager.getRegisteredConfigInstance(GeneralConfigManager.class).getGemCacheExpireTime();
    }

    public void initLater() {
        gemManager.initLater();
        configManager.lateInit();
        if (configManager.getRegisteredConfigInstance(GeneralConfigManager.class).canRunUpdater())
            updaterManager.start();
        recipeManager.initiateRecipes();
    }

    public void initAfterAddonLoad() {

    }
}
