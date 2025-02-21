package dev.iseal.powergems.gems.powerClasses.tasks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;

public class PernamentEffectsGiver implements Runnable {

    private static final int EFFECT_DURATION = 100;
    
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final Map<String, PotionEffectType> gemEffects = new HashMap<>();

    public PernamentEffectsGiver() {
        // Register all gem effects
        gemEffects.put("Fire", PotionEffectType.FIRE_RESISTANCE);
        gemEffects.put("Strength", PotionEffectType.INCREASE_DAMAGE);
        gemEffects.put("Healing", PotionEffectType.REGENERATION);
        gemEffects.put("Air", PotionEffectType.SPEED);
        gemEffects.put("Iron", PotionEffectType.DAMAGE_RESISTANCE);
        gemEffects.put("Lightning", PotionEffectType.FAST_DIGGING);
        gemEffects.put("Ice", PotionEffectType.HEALTH_BOOST);
        gemEffects.put("Lava", PotionEffectType.JUMP);
        gemEffects.put("Water", PotionEffectType.WATER_BREATHING);
        gemEffects.put("Sand", PotionEffectType.SATURATION);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Map.Entry<String, PotionEffectType> entry : gemEffects.entrySet()) {
                checkAndApplyEffect(player, entry.getKey(), entry.getValue());
            }
        }
    }

    private void checkAndApplyEffect(Player player, String gemType, PotionEffectType effectType) {
        boolean hasHighLevelGem = gemManager.hasGemLevel3OrHigher(player, gemType);
        PotionEffect existing = player.getPotionEffect(effectType);
        
        if (hasHighLevelGem) {
            if (existing == null || (existing.getAmplifier() == 0 && existing.getDuration() <= 5)) {
                player.addPotionEffect(new PotionEffect(
                    effectType,
                    EFFECT_DURATION,
                    0,
                    false,
                    false
                ));
            }
        }
    }
}
