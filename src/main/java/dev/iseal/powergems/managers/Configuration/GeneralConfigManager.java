package dev.iseal.powergems.managers.Configuration;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import de.leonhard.storage.Config;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class GeneralConfigManager extends AbstractConfigManager {

    public GeneralConfigManager() {
        super(null);
        file = new Config("config", PowerGems.getPlugin().getDataFolder()+"");
    }

    public void setUpConfig() {
        // WARNING: Using PowerGems.file is deprecated and should be replaced with a getter in the respective class. I'm just too lazy to do it.
        PowerGems.config = file;
        file.setDefault("pluginPrefix", ChatColor.BLACK + "[" + ChatColor.DARK_RED + "PowerGems" + ChatColor.BLACK + "] ");
        file.setDefault("allowOnlyOneGem", false);
        file.setDefault("useNewAllowOnlyOneGemAlgorithm", true);
        file.setDefault("canDropGems", false);
        file.setDefault("giveGemOnFirstLogin", true);
        file.setDefault("canUpgradeGems", true);
        file.setDefault("canCraftGems", true);
        file.setDefault("keepGemsOnDeath", true);
        file.setDefault("gemsHaveDescriptions", true);
        file.setDefault("explosionDamageAllowed", true);
        file.setDefault("preventGemPowerTampering", true);
        file.setDefault("doGemDecay", true);
        file.setDefault("doGemDecayOnLevel1", false);
        file.setDefault("dragonEggHalfCooldown", true);
        file.setDefault("randomizedColors", false);
        file.setDefault("allowMovingGems", false);
        file.setDefault("doDebuffForTemperature", true);
        file.setDefault("attemptFixOldGems", false);
        file.setDefault("cooldownBoostPerLevelInSeconds", 2L);
        file.setDefault("delayToUseGemsOnJoin", 30);
        file.setDefault("gemCreationAttempts", 10);
        file.setDefault("gemCacheExpireTime", 60);
        file.setDefault("allowMetrics", true);
        file.setDefault("blockedReplacingBlocks",
                new Material[] { Material.BEDROCK, Material.WATER, Material.NETHERITE_BLOCK });
        file.setDefault("debugMode", false);
        file.setDefault("runUpdater", true);
        file.setDefault("maxGemLevel", 5);
        file.setDefault("allowCosmeticParticleEffects", true);
        file.setDefault("cosmeticParticleEffectInterval", 5L);
        file.setDefault("isWorldGuardSupportEnabled", true);
        file.setDefault("languageCode", "en");
        file.setDefault("countryCode", "US");
    }

    @Override
    public void lateInit() {

    }

    public long getGemCooldownBoost() {
        return file.getLong("cooldownBoostPerLevelInSeconds");
    }
    public boolean isDragonEggHalfCooldown() {
        return file.getBoolean("dragonEggHalfCooldown");
    }
    public boolean getGiveGemOnFirstLogin() {
        return file.getBoolean("giveGemOnFirstLogin");
    }
    public long getDelayToUseGems() {
        return file.getLong("delayToUseGemsOnJoin");
    }
    public boolean isRandomizedColors() {
        return file.getBoolean("randomizedColors");
    }
    public boolean doGemDescriptions() {
        return file.getBoolean("gemsHaveDescriptions");
    }
    public boolean allowOnlyOneGem() {
        return file.getBoolean("allowOnlyOneGem");
    }
    public int getGemCreationAttempts() {
        return file.getInt("gemCreationAttempts");
    }
    public boolean isAllowMetrics() {
        return file.getBoolean("allowMetrics");
    }
    public boolean doGemDecay() {
        return file.getBoolean("doGemDecay");
    }
    public String getPluginPrefix() {
        return file.getString("pluginPrefix");
    }
    public boolean canUpgradeGems() {
        return file.getBoolean("canUpgradeGems");
    }
    public boolean canCraftGems() {
        return file.getBoolean("canCraftGems");
    }
    public boolean isExplosionDamageAllowed() {
        return file.getBoolean("isExplosionDamageAllowed");
    }
    public boolean useNewAllowOnlyOneGemAlgorithm() {
        return file.getBoolean("useNewAllowOnlyOneGemAlgorithm");
    }
    public boolean doKeepGemsOnDeath() {
        return file.getBoolean("keepGemsOnDeath");
    }
    public boolean doGemDecayOnLevelOne() {
        return file.getBoolean("doGemDecayOnLevel1");
    }
    public boolean isDebugMode() {
        return file.getBoolean("debugMode");
    }
    public boolean canRunUpdater() {
        return file.getBoolean("runUpdater");
    }
    public boolean isBlockedReplacingBlock(Block block) {
        List<String> blocks = file.getStringList("blockedReplacingBlocks");
        for (String mat : blocks) {
            Material material = Material.valueOf(mat);
            if (block.getType().equals(material)) {
                return true;
            }
        }
        return false;
    }
    public int getMaxGemLevel() {
        return file.getInt("maxGemLevel");
    }
    public long cosmeticParticleEffectInterval() {
        return file.getLong("cosmeticParticleEffectInterval");
    }
    public boolean allowCosmeticParticleEffects() {
        return file.getBoolean("allowCosmeticParticleEffects");
    }
    public boolean doGemPowerTampering() {
        return file.getBoolean("preventGemPowerTampering");
    }
    public boolean canDropGems() {
        return file.getBoolean("canDropGems");
    }
    public boolean isWorldGuardEnabled() {
        return file.getBoolean("isWorldGuardSupportEnabled");
    }
    public String getLanguageCode() {
        return file.getString("languageCode");
    }
    public String getCountryCode() {
        return file.getString("countryCode");
    }
    public int getGemCacheExpireTime() {
        return file.getInt("gemCacheExpireTime");
    }
    public boolean doDebuffForTemperature() {
        return file.getBoolean("doDebuffForTemperature");
    }
    public boolean doAttemptFixOldGems() {
        return file.getBoolean("attemptFixOldGems");
    }
}
