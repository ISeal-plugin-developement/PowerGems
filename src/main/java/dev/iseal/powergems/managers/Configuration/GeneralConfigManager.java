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
        file.setDefault("allowCosmeticParticleEffects", true);
        file.setDefault("allowMetrics", true);
        file.setDefault("allowMovingGems", false);
        file.setDefault("allowOnlyOneGem", false);
        file.setDefault("attemptFixOldGems", true);
        file.setDefault("blockedReplacingBlocks",
                new Material[] { Material.BEDROCK, Material.WATER, Material.NETHERITE_BLOCK });
        file.setDefault("canCraftGems", true);
        file.setDefault("canDropGems", false);
        file.setDefault("canUpgradeGems", true);
        file.setDefault("cooldownBoostPerLevelInSeconds", 2L);
        file.setDefault("cosmeticParticleEffectInterval", 5L);
        file.setDefault("countryCode", "US");
        file.setDefault("debugMode", false);
        file.setDefault("delayToUseGemsOnJoin", 30);
        file.setDefault("doDebuffForTemperature", true);
        file.setDefault("doGemDecay", true);
        file.setDefault("doGemDecayOnLevel1", false);
        file.setDefault("dragonEggHalfCooldown", true);
        file.setDefault("explosionDamageAllowed", true);
        file.setDefault("gemCacheExpireTime", 10);
        file.setDefault("gemCreationAttempts", 10);
        file.setDefault("gemsHaveDescriptions", true);
        file.setDefault("giveGemOnFirstLogin", true);
        file.setDefault("unlockNewAbilitiesOnLevelX", 3);
        file.setDefault("giveGemPermanentEffectOnLevelX", true);
        file.setDefault("unlockShiftAbilityOnLevelX", false);
        file.setDefault("isWorldGuardSupportEnabled", true);
        file.setDefault("keepGemsOnDeath", true);
        file.setDefault("languageCode", "en");
        file.setDefault("maxGemLevel", 5);
        file.setDefault("pluginPrefix", ChatColor.BLACK + "[" + ChatColor.DARK_RED + "PowerGems" + ChatColor.BLACK + "] ");
        file.setDefault("preventGemPowerTampering", true);
        file.setDefault("randomizedColors", false);
        file.setDefault("runUpdater", true);
        file.setDefault("upgradeGemOnKill", false);
        file.setDefault("useNewAllowOnlyOneGemAlgorithm", true);

    }

    @Override
    public void lateInit() {
    }
    
    // Methods ordered to match configuration keys
    public boolean allowCosmeticParticleEffects() {
        return file.getBoolean("allowCosmeticParticleEffects");
    }

    public boolean isAllowMetrics() {
        return file.getBoolean("allowMetrics");
    }

    public boolean isAllowMovingGems() {
        return file.getBoolean("allowMovingGems");
    }

    public boolean allowOnlyOneGem() {
        return file.getBoolean("allowOnlyOneGem");
    }

    public boolean doAttemptFixOldGems() {
        return file.getBoolean("attemptFixOldGems");
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

    public boolean canCraftGems() {
        return file.getBoolean("canCraftGems");
    }

    public boolean canDropGems() {
        return file.getBoolean("canDropGems");
    }

    public boolean canUpgradeGems() {
        return file.getBoolean("canUpgradeGems");
    }

    public long getGemCooldownBoost() {
        return file.getLong("cooldownBoostPerLevelInSeconds");
    }

    public long cosmeticParticleEffectInterval() {
        return file.getLong("cosmeticParticleEffectInterval");
    }

    public String getCountryCode() {
        return file.getString("countryCode");
    }

    public boolean isDebugMode() {
        return file.getBoolean("debugMode");
    }

    public long getDelayToUseGems() {
        return file.getLong("delayToUseGemsOnJoin");
    }

    public boolean doDebuffForTemperature() {
        return file.getBoolean("doDebuffForTemperature");
    }

    public boolean doGemDecay() {
        return file.getBoolean("doGemDecay");
    }

    public boolean doGemDecayOnLevelOne() {
        return file.getBoolean("doGemDecayOnLevel1");
    }

    public boolean isDragonEggHalfCooldown() {
        return file.getBoolean("dragonEggHalfCooldown");
    }

    public boolean isExplosionDamageAllowed() {
        return file.getBoolean("explosionDamageAllowed");
    }

    public int getGemCacheExpireTime() {
        return file.getInt("gemCacheExpireTime");
    }

    public int getGemCreationAttempts() {
        return file.getInt("gemCreationAttempts");
    }

    public boolean doGemDescriptions() {
        return file.getBoolean("gemsHaveDescriptions");
    }

    public boolean getGiveGemOnFirstLogin() {
        return file.getBoolean("giveGemOnFirstLogin");
    }

    public boolean giveGemPermanentEffectOnLvlX() {
        return file.getBoolean("giveGemPermanentEffectOnLevelX");
    }

    public boolean isWorldGuardEnabled() {
        return file.getBoolean("isWorldGuardSupportEnabled");
    }

    public boolean doKeepGemsOnDeath() {
        return file.getBoolean("keepGemsOnDeath");
    }

    public String getLanguageCode() {
        return file.getString("languageCode");
    }

    public int getMaxGemLevel() {
        return file.getInt("maxGemLevel");
    }

    public String getPluginPrefix() {
        return file.getString("pluginPrefix");
    }

    public boolean doGemPowerTampering() {
        return file.getBoolean("preventGemPowerTampering");
    }

    public boolean isRandomizedColors() {
        return file.getBoolean("randomizedColors");
    }

    public boolean canRunUpdater() {
        return file.getBoolean("runUpdater");
    }

    public boolean upgradeGemOnKill() {
        return file.getBoolean("upgradeGemOnKill");
    }

    public boolean useNewAllowOnlyOneGemAlgorithm() {
        return file.getBoolean("useNewAllowOnlyOneGemAlgorithm");
    }

    public boolean unlockShiftAbilityOnLevelX() {
        return file.getBoolean("unlockShiftAbilityOnLevelX");
    }

    public int unlockNewAbilitiesOnLevelX() {
        return file.getInt("unlockNewAbilitiesOnLevelX");
    }
}
