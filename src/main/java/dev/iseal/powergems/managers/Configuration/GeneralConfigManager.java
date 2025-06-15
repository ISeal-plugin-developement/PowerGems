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
        file.setDefault("#pluginPrefix", "Prefix displayed in messages sent by the plugin");
        file.setDefault("pluginPrefix", ChatColor.BLACK + "[" + ChatColor.DARK_RED + "PowerGems" + ChatColor.BLACK + "] ");
        
        file.setDefault("#allowCosmeticParticleEffects", "Enables visual particle effects around players with active gems");
        file.setDefault("allowCosmeticParticleEffects", true);
        
        file.setDefault("#allowMetrics", "Enables anonymous usage statistics collection via bStats");
        file.setDefault("allowMetrics", true);
        
        file.setDefault("#allowMovingGems", "When true, players can move gems between inventories (otherwise gems are locked to inventory)");
        file.setDefault("allowMovingGems", false);
        
        file.setDefault("#allowOnlyOneGem", "When true, players can only have one active gem at a time");
        file.setDefault("allowOnlyOneGem", false);
        
        file.setDefault("#attemptFixOldGems", "Automatically attempts to repair gems from older versions when detected");
        file.setDefault("attemptFixOldGems", true);
        
        file.setDefault("#blockedReplacingBlocks", "List of materials that cannot be replaced by gem abilities");
        file.setDefault("blockedReplacingBlocks",
                new Material[] { Material.BEDROCK, Material.WATER, Material.NETHERITE_BLOCK });
        
        file.setDefault("#canCraftGems", "Enables or disables gem crafting recipes");
        file.setDefault("canCraftGems", true);
        
        file.setDefault("#canDropGems", "When true, players can drop gems from their inventory");
        file.setDefault("canDropGems", false);
        
        file.setDefault("#canUpgradeGems", "Allows gems to be upgraded to higher levels");
        file.setDefault("canUpgradeGems", true);
        
        file.setDefault("#cooldownBoostPerLevelInSeconds", "Reduction in cooldown time (in seconds) for each gem level");
        file.setDefault("cooldownBoostPerLevelInSeconds", 2L);
        
        file.setDefault("#cosmeticParticleEffectInterval", "Time in ticks between particle effect updates");
        file.setDefault("cosmeticParticleEffectInterval", 5L);
        
        file.setDefault("#countryCode", "Country code used for localization formatting");
        file.setDefault("countryCode", "US");
        
        file.setDefault("#debugMode", "Enables additional logging information for troubleshooting");
        file.setDefault("debugMode", false);
        
        file.setDefault("#delayToUseGemsOnJoin", "Time in seconds before players can use gems after joining the server");
        file.setDefault("delayToUseGemsOnJoin", 30);
        
        file.setDefault("#doDebuffForTemperature", "Apply environmental effects to players based on gem temperature compatibility");
        file.setDefault("doDebuffForTemperature", true);
        
        file.setDefault("#doGemDecay", "Gems lose 1 level when you die");
        file.setDefault("doGemDecay", true);
        
        file.setDefault("#doGemDecayOnLevel1", "Whether level 1 gems should decay(basically get destroyed when you die  with a lvl 1 gem)");
        file.setDefault("doGemDecayOnLevel1", false);
        
        file.setDefault("#dragonEggHalfCooldown", "Players with Dragon Egg in inventory get reduced gem cooldowns");
        file.setDefault("dragonEggHalfCooldown", true);
        
        file.setDefault("#explosionDamageAllowed", "Whether gem abilities can cause explosion damage to blocks");
        file.setDefault("explosionDamageAllowed", true);
        
        file.setDefault("#gemCacheExpireTime", "Time in seconds before cached gem data expires");
        file.setDefault("gemCacheExpireTime", 60);
        
        file.setDefault("#gemCreationAttempts", "Number of attempts to generate a valid random gem before giving up");
        file.setDefault("gemCreationAttempts", 10);
        
        file.setDefault("#gemsHaveDescriptions", "Show descriptive lore text on gem items");
        file.setDefault("gemsHaveDescriptions", true);
        
        file.setDefault("#giveGemOnFirstLogin", "Give a random gem to players when they first join the server");
        file.setDefault("giveGemOnFirstLogin", true);
<<<<<<< HEAD
        
        file.setDefault("#giveGemPermanentEffectOnLvl3", "Level 3+ gems provide passive effects without activation");
        file.setDefault("giveGemPermanentEffectOnLvl3", false);
        
        file.setDefault("#isWorldGuardSupportEnabled", "Enable integration with WorldGuard");
=======
        file.setDefault("unlockNewAbilitiesOnLevelX", 3);
        file.setDefault("giveGemPermanentEffectOnLevelX", true);
        file.setDefault("unlockShiftAbilityOnLevelX", false);
>>>>>>> 8bedf562cce08fed7e5d5f10fb00550cd5ef0461
        file.setDefault("isWorldGuardSupportEnabled", true);
        
        file.setDefault("#keepGemsOnDeath", "Players keep their gems when they die");
        file.setDefault("keepGemsOnDeath", true);
        
        file.setDefault("#languageCode", "Language code for plugin messages (en, de, etc.)");
        file.setDefault("languageCode", "en");
        
        file.setDefault("#maxGemLevel", "Maximum level gems can reach through upgrades");
        file.setDefault("maxGemLevel", 5);
        
        file.setDefault("#pluginPrefix", "Prefix displayed in messages sent by the plugin");
        file.setDefault("pluginPrefix", ChatColor.BLACK + "[" + ChatColor.DARK_RED + "PowerGems" + ChatColor.BLACK + "] ");
        
        file.setDefault("#preventGemPowerTampering", "Prevents players from modifying gem powers through commands/exploits");
        file.setDefault("preventGemPowerTampering", true);
        
        file.setDefault("#randomizedColors", "Use random colors for gem names instead of preset colors");
        file.setDefault("randomizedColors", false);
        
        file.setDefault("#runUpdater", "Check for plugin updates on startup");
        file.setDefault("runUpdater", true);
        
        file.setDefault("#upgradeGemOnKill", "Gems level up when you kill a player");
        file.setDefault("upgradeGemOnKill", false);
        
        file.setDefault("#useNewAllowOnlyOneGemAlgorithm", "Use improved detection algorithm for the 'allowOnlyOneGem' feature");
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
