package me.iseal.powergems.managers;

import me.iseal.powergems.listeners.FallingBlockHitListener;
import me.iseal.powergems.listeners.powerListeners.IceTargetListener;
import me.iseal.powergems.listeners.powerListeners.SandMoveListener;
import me.iseal.powergems.listeners.powerListeners.StrenghtMoveListener;
import me.iseal.powergems.misc.Utils;

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

    public void init() {
        configManager = new ConfigManager();
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
    }

    public void initLater() {
        gemManager.initLater();
        updaterManager.start();
        configManager.setUpConfig();
        recipeManager.initiateRecipes();
    }
}
