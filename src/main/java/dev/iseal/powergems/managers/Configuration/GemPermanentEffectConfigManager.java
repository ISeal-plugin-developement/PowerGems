package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Objects;
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
        if (Objects.equals(file.getString(gemName + "GemEffect"), "NONE")) {
            CACHE.put(gemName, null);
            return null;
        }

        String effectName = file.getString(gemName + "GemEffect");
        PotionEffectType effectType;

        try {
            NamespacedKey key = NamespacedKey.minecraft(effectName.toLowerCase());
            effectType = Registry.EFFECT.get(key);
            if (effectType == null) {
                effectType = PotionEffectType.getByName(effectName);
            }
        } catch (Exception e) {
            effectType = PotionEffectType.getByName(effectName);
        }

        CACHE.put(gemName, effectType);
        return effectType;
    }
}
