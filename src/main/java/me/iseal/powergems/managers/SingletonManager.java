package me.iseal.powergems.managers;

import me.iseal.powergems.listeners.fallingBlockHitListener;
import me.iseal.powergems.listeners.powerListeners.iceTargetListener;
import me.iseal.powergems.listeners.powerListeners.sandMoveListener;
import me.iseal.powergems.listeners.powerListeners.strenghtMoveListener;
import me.iseal.powergems.misc.Utils;

public class SingletonManager {

    //Classes

    public GemManager gemManager;
    public strenghtMoveListener strenghtMoveListen;
    public Utils utils;
    public RecipeManager recipeManager;
    public UpdaterManager updaterManager;
    public ConfigManager configManager;
    public CooldownManager cooldownManager;
    public sandMoveListener sandMoveListen;
    public iceTargetListener iceTargetListen;
    public fallingBlockHitListener fallingBlockHitListen;
    public TempDataManager tempDataManager;

    public void init(){
        configManager = new ConfigManager();
        updaterManager = new UpdaterManager();
        tempDataManager = new TempDataManager();
        sandMoveListen = new sandMoveListener();
        iceTargetListen = new iceTargetListener();
        fallingBlockHitListen = new fallingBlockHitListener();
        gemManager = new GemManager();
        strenghtMoveListen = new strenghtMoveListener();
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
