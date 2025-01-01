package dev.iseal.powergems.managers;

import dev.iseal.powergems.listeners.FallingBlockHitListener;
import dev.iseal.powergems.listeners.powerListeners.IceTargetListener;
import dev.iseal.powergems.listeners.powerListeners.SandMoveListener;
import dev.iseal.powergems.listeners.powerListeners.StrenghtMoveListener;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.sealLib.Metrics.MetricsManager;

public class SingletonManager {

    public static int TOTAL_GEM_AMOUNT = 0;
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
    public MetricsManager metricsManager;

    public void init() {
        configManager = ConfigManager.getInstance();
        configManager.setUpConfig();
        namespacedKeyManager = new NamespacedKeyManager();
        updaterManager = UpdaterManager.getInstance();
        tempDataManager = new TempDataManager();
        sandMoveListen = new SandMoveListener();
        iceTargetListen = new IceTargetListener();
        fallingBlockHitListen = new FallingBlockHitListener();
        gemManager = GemManager.getInstance();
        strenghtMoveListen = new StrenghtMoveListener();
        utils = new Utils();
        recipeManager = RecipeManager.getInstance();
        cooldownManager = CooldownManager.getInstance();
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
