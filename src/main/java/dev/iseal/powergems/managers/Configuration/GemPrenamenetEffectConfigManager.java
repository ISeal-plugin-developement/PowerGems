package dev.iseal.powergems.managers.Configuration;

import org.bukkit.potion.PotionEffectType;

import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class GemPrenamenetEffectConfigManager extends AbstractConfigManager {

    public GemPrenamenetEffectConfigManager() {
        super("GemPermanentEffects");
    }

    @Override
    public void setUpConfig() {
        file.setDefault("FireGemEffect", PotionEffectType.FIRE_RESISTANCE.getName());
        file.setDefault("StrengthGemEffect", PotionEffectType.INCREASE_DAMAGE.getName());
        file.setDefault("HealingGemEffect", PotionEffectType.REGENERATION.getName());
        file.setDefault("AirGemEffect", PotionEffectType.SPEED.getName());
        file.setDefault("IronGemEffect", PotionEffectType.DAMAGE_RESISTANCE.getName());
        file.setDefault("LightningGemEffect", PotionEffectType.FAST_DIGGING.getName());
        file.setDefault("IceGemEffect", PotionEffectType.HEALTH_BOOST.getName());
        file.setDefault("LavaGemEffect", PotionEffectType.JUMP.getName());
        file.setDefault("WaterGemEffect", PotionEffectType.WATER_BREATHING.getName());
        file.setDefault("SandGemEffect", PotionEffectType.SATURATION.getName());
    }

    @Override
    public void lateInit() {
    }

    /**
     * @return The PotionEffectType configured for this gem
     */
    public PotionEffectType getGemEffect(String gemName) {
        String effectName = file.getString(gemName + "GemEffect");
        try {
            return PotionEffectType.getByName(effectName);
        } catch (IllegalArgumentException e) {
            return getDefaultEffect(gemName);
        }
    }

    private PotionEffectType getDefaultEffect(String gemName) {
        return switch (gemName) {
            case "Fire" -> PotionEffectType.FIRE_RESISTANCE;
            case "Strength" -> PotionEffectType.INCREASE_DAMAGE;
            case "Healing" -> PotionEffectType.REGENERATION;
            case "Air" -> PotionEffectType.SPEED;
            case "Iron" -> PotionEffectType.DAMAGE_RESISTANCE;
            case "Lightning" -> PotionEffectType.FAST_DIGGING;
            case "Ice" -> PotionEffectType.HEALTH_BOOST;
            case "Lava" -> PotionEffectType.JUMP;
            case "Water" -> PotionEffectType.LUCK;
            case "Sand" -> PotionEffectType.SATURATION;
            default -> PotionEffectType.LUCK;
        };
    }
}
