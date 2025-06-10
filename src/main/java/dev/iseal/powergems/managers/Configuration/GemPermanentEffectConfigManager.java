package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.logging.Level;

public class GemPermanentEffectConfigManager extends AbstractConfigManager {
    public GemPermanentEffectConfigManager() {
        super("GemPermanentEffects");
    }

    private final HashMap<String, PotionEffectType> CACHE = new HashMap<>();

    @Override
    public void setUpConfig() {

    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            createDefaultEffectSettings(i);
        }
    }

    private void createDefaultEffectSettings(int i) {
        if (file.contains(GemManager.lookUpName(i) + "GemEffect")) return;
        if (i == -1) ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid gem ID: " + i), Level.WARNING, "CREATE_DEFAULT_EFFECT_SETTINGS");
        // this is dogshit. I'm sorry.
        file.setDefault(GemManager.lookUpName(i) + "GemEffect", GemReflectionManager.getInstance().getSingletonGemInstance(GemManager.lookUpName(i)).getDefaultEffectType().getName());
    }

    public PotionEffectType getType(String gemName) {
        if (CACHE.containsKey(gemName)) return CACHE.get(gemName);
        CACHE.put(gemName, PotionEffectType.getByName(file.getString(gemName+ "GemEffect")));
        return CACHE.get(gemName);
    }
}
