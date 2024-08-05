package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import me.iseal.powergems.Main;
import me.iseal.powergems.misc.AbstractConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.UUID;

public class GeneralConfigManager extends AbstractConfigManager {

    UUID id = UUID.randomUUID();

    public void setUpConfig() {
        file = new Config("config", Main.getPlugin().getDataFolder()+"");
        // WARNING: Using Main.file is deprecated and should be replaced with a getter in the respective class. I'm just too lazy to do it.
        Main.config = file;
        file.setDefault("pluginPrefix", ChatColor.BLACK + "[" + ChatColor.DARK_RED + "PowerGems" + ChatColor.BLACK + "] ");
        file.setDefault("allowOnlyOneGem", false);
        file.setDefault("useNewAllowOnlyOneGemAlgorithm", true);
        file.setDefault("canDropGems", false);
        file.setDefault("giveGemOnFirstLogin", true);
        file.setDefault("canUpgradeGems", true);
        file.setDefault("canCraftGems", true);
        file.setDefault("keepGemsOnDeath", true);
        file.setDefault("gemsHaveDescriptions", false);
        file.setDefault("explosionDamageAllowed", true);
        file.setDefault("preventGemPowerTampering", true);
        file.setDefault("doGemDecay", true);
        file.setDefault("doGemDecayOnLevel1", false);
        file.setDefault("dragonEggHalfCooldown", true);
        file.setDefault("randomizedColors", false);
        file.setDefault("allowMovingGems", false);
        file.setDefault("cooldownBoostPerLevelInSeconds", 2L);
        file.setDefault("delayToUseGemsOnJoin", 30);
        file.setDefault("gemCreationAttempts", 10);
        file.setDefault("allowBStatsMetrics", true);
        file.setDefault("blockedReplacingBlocks",
                new Material[] { Material.BEDROCK, Material.WATER, Material.NETHERITE_BLOCK });
        file.setDefault("debugMode", false);
        file.setDefault("runUpdater", true);

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
        ArrayList<String> blocks = (ArrayList<String>) file.get("blockedReplacingBlocks");
        for (String mat : blocks) {
            Material material = Material.valueOf(mat);
            if (block.getType().equals(material)) {
                return true;
            }
        }
        return false;
    }
}
