package dev.iseal.powergems.managers.Configuration;

import java.util.logging.Level;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.sealUtils.utils.ExceptionHandler;

public class GemPermanentEffectLevelConfigManager extends AbstractConfigManager {
    public GemPermanentEffectLevelConfigManager() {
        super("GemPermanentEffectLevels");
    }

    @Override
    public void setUpConfig() {
        file.setHeader(
                "PowerGems - Permanent Effect Levels Configuration",
                "",
                "This config file manages the permanent potion effects applied by gems.",
                "Change the numbers to modify the effect amplifier (level-1).",
                "",
                "== Available Potion Effects (In-game Name = Spigot Name) ==",
                "",
                "Strength = INCREASE_DAMAGE",
                "Speed = SPEED",
                "Resistance = DAMAGE_RESISTANCE",
                "Regeneration = REGENERATION",
                "Fire Resistance = FIRE_RESISTANCE",
                "Water Breathing = WATER_BREATHING",
                "Jump Boost = JUMP",
                "Night Vision = NIGHT_VISION",
                "Health Boost = HEALTH_BOOST",
                "Absorption = ABSORPTION",
                "Haste = FAST_DIGGING",
                "Mining Fatigue = SLOW_DIGGING",
                "Slowness = SLOW",
                "Weakness = WEAKNESS",
                "Poison = POISON",
                "Wither = WITHER",
                "Hunger = HUNGER",
                "Saturation = SATURATION",
                "Invisibility = INVISIBILITY",
                "Blindness = BLINDNESS",
                "Nausea = CONFUSION",
                "Glowing = GLOWING",
                "Levitation = LEVITATION",
                "Luck = LUCK",
                "Bad Luck = UNLUCK",
                "Slow Falling = SLOW_FALLING",
                "Conduit Power = CONDUIT_POWER",
                "Dolphins Grace = DOLPHINS_GRACE",
                "",
                "For more details: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html"
        );
    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            createDefaultLevelSettings(i);
        }
    }

    private void createDefaultLevelSettings(int i) {
        if (file.contains(GemManager.lookUpName(i) + "GemEffectLevel")) return;
        if (i == -1) ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid gem ID: " + i), Level.WARNING, "CREATE_DEFAULT_LEVEL_SETTINGS");
        file.setDefault(
                GemManager.lookUpName(i) + "GemEffectLevel",
                GemReflectionManager.getInstance().getSingletonGemInstance(GemManager.lookUpName(i)).getDefaultEffectLevel()
        );
    }

    public int getLevel(String gemName) {
        return file.getInt(gemName + "GemEffectLevel");
    }
}
