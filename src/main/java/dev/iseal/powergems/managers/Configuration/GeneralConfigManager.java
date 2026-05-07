package dev.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class GeneralConfigManager extends AbstractConfigManager {

    public GeneralConfigManager() {
        super(null);
        file = new Config("config", PowerGems.getPlugin().getDataFolder().getPath());
    }

    private static final Integer CONFIG_VERSION = 1;

    private final Logger logger = PowerGems.getPlugin().getLogger();
    private final TempDataManager tdm = SingletonManager.getInstance().tempDataManager;

    private final Map<Integer, String> upgradeWarnings = Map.of(
            1, "In GemColor.yml the colors now use Hex color codes. All previous colors will not work. \n" +
                    "Please regenerate the config or rewrite it yourself"
    );

    public void setUpConfig() {
        int lastConfigVersion = tdm.readDataFromFile("lastConfigVersion") == null ? 0 : (int) tdm.readDataFromFile("lastConfigVersion");
        if (lastConfigVersion != CONFIG_VERSION) {
            for (int i = lastConfigVersion+1; i <= CONFIG_VERSION; i++) {
                if (upgradeWarnings.containsKey(i)) logger.warning(upgradeWarnings.get(i));
            }
            tdm.writeDataToFile("lastConfigVersion", CONFIG_VERSION);
        }

        file.set("configVersion", CONFIG_VERSION);
        file.setDefault("gemStartingLevel", 1);
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
        file.setDefault("gemCacheExpireTime", 5);
        file.setDefault("gemCreationAttempts", 10);
        file.setDefault("gemsHaveDescriptions", true);
        file.setDefault("giveGemOnFirstLogin", true);
        file.setDefault("unlockNewAbilitiesOnLevelX", 3);
        file.setDefault("giveGemPermanentEffectOnLevelX", true);
        file.setDefault("unlockShiftAbilityOnLevelX", false);
        file.setDefault("isWorldGuardSupportEnabled", true);
        file.setDefault("isCombatLogXSupportEnabled", true);
        file.setDefault("keepGemsOnDeath", true);
        file.setDefault("languageCode", "en");
        file.setDefault("maxGemLevel", 5);
        file.setDefault("preventGemPowerTampering", true);
        file.setDefault("randomizedColors", false);
        file.setDefault("runUpdater", true);
        file.setDefault("upgradeGemOnKill", false);
        file.setDefault("analyticsID", generateAnalyticsId());
    }

    @Override
    public void lateInit() {
    }

    public static String generateAnalyticsId() {
        int number = ThreadLocalRandom.current().nextInt(0, 1_000_000_000);
        return String.format("AA-%09d", number);
    }

    public boolean allowCosmeticParticleEffects() {
        return file.getBoolean("allowCosmeticParticleEffects");
    }

    public boolean isAllowMetrics() {
        return file.getBoolean("allowMetrics");
    }

    public String getAnalyticsID() {
        return file.getString("analyticsID");
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

    public boolean isCombatLogXEnabled() {
        return file.getBoolean("isCombatLogXSupportEnabled");
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

    public boolean unlockShiftAbilityOnLevelX() {
        return file.getBoolean("unlockShiftAbilityOnLevelX");
    }

    public int unlockNewAbilitiesOnLevelX() {
        return file.getInt("unlockNewAbilitiesOnLevelX");
    }
}
