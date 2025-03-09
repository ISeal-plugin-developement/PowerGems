package dev.iseal.powergems.gems.powerClasses.tasks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.Configuration.GemPrenamenetEffectConfigManager;

public class PernamentEffectsGiver extends BukkitRunnable {

    private static final int EFFECT_DURATION = 100;
    
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final GemPrenamenetEffectConfigManager effectConfig = SingletonManager.getInstance()
            .configManager.getRegisteredConfigInstance(GemPrenamenetEffectConfigManager.class);
    private final Map<String, PotionEffectType> gemEffects = new HashMap<>();

    public PernamentEffectsGiver() {
        loadEffects();
    }

    private void loadEffects() {
        String[] gemTypes = {"Fire", "Strength", "Healing", "Air", "Iron", 
                        "Lightning", "Ice", "Lava", "Water", "Sand"};
                        
        for (String gemType : gemTypes) {
            PotionEffectType effect = effectConfig.getGemEffect(gemType);
            if (effect != null) {
                gemEffects.put(gemType, effect);
            }
        }
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            gemEffects.forEach((gemType, effectType) -> 
                checkAndApplyEffect(player, gemType, effectType));
        }
    }

    private void checkAndApplyEffect(Player player, String gemType, PotionEffectType effectType) {
        boolean hasHighLevelGem = gemManager.hasGemLevel3OrHigher(player, gemType);
        PotionEffect existing = player.getPotionEffect(effectType);
        
        if (hasHighLevelGem) {
            if (existing == null || 
            (existing.getAmplifier() == 0 && existing.getDuration() <= 5)) {
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
