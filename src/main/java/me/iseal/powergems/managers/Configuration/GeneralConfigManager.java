package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import de.leonhard.storage.Yaml;
import me.iseal.powergems.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class GeneralConfigManager extends AbstractConfigManager {

    public void setUpConfig() {
        file = new Config("config", Main.getPlugin().getDataFolder().getPath());
        // WARNING: Using Main.file is deprecated and should be replaced with a getter in the respective class. I'm just too lazy to do it.
        Main.config = file;
        file.setDefault("pluginPrefix", ChatColor.BLACK + "[" + ChatColor.DARK_RED + "PowerGems" + ChatColor.BLACK + "] ");
        file.setDefault("allowOnlyOneGem", false);
        file.setDefault("canDropGems", false);
        file.setDefault("giveGemOnFirstLogin", true);
        file.setDefault("canUpgradeGems", true);
        file.setDefault("canCraftGems", true);
        file.setDefault("keepGemsOnDeath", true);
        file.setDefault("gemsHaveDescriptions", false);
        file.setDefault("explosionDamageAllowed", true);
        file.setDefault("preventGemPowerTampering", true);
        file.setDefault("doGemDecay", true);
        file.setDefault("doDecayOnLevel1", false);
        file.setDefault("dragonEggHalfCooldown", true);
        file.setDefault("randomizedColors", false);
        file.setDefault("allowMovingGems", false);
        file.setDefault("cooldownBoostPerLevelInSeconds", 2L);
        file.setDefault("delayToUseGemsOnJoin", 30);
        file.setDefault("gemCreationAttempts", 10);
        file.setDefault("allowBStatsMetrics", true);
        file.setDefault("blockedLavaBlocks",
                new Material[] { Material.BEDROCK, Material.WATER, Material.NETHERITE_BLOCK });
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
    public boolean allowOnlyOneGem() {
        return file.getBoolean("allowOnlyOneGem");
    }
    public int getGemCreationAttempts() {
        return file.getInt("gemCreationAttempts");
    }
    public boolean doGemDescriptions() {
        return file.getBoolean("gemsHaveDescriptions");
    }
    public boolean isAllowBStatsMetrics() {
        return file.getBoolean("allowBStatsMetrics");
    }
    public boolean doGemDecay() {
        return file.getBoolean("doGemDecay");
    }
    public String getPluginPrefix() {
        return file.getString("pluginPrefix");
    }
}
