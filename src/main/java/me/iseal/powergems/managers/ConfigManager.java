package me.iseal.powergems.managers;

import de.leonhard.storage.Config;
import de.leonhard.storage.Yaml;
import me.iseal.powergems.Main;
import org.bukkit.Material;

public class ConfigManager {
    Config config = null;
    Yaml activeGems = null;
    Yaml cooldowns = null;

    public void setUpConfig(){
        config = new Config("config", Main.getPlugin().getDataFolder().getPath());
        activeGems = new Yaml("gem_active", Main.getPlugin().getDataFolder()+"\\config\\");
        cooldowns = new Yaml("cooldowns", Main.getPlugin().getDataFolder()+"\\config\\");
        Main.config = config;
        config.setDefault("allowOnlyOneGem", false);
        config.setDefault("canDropGems", false);
        config.setDefault("giveGemOnFirstLogin", true);
        config.setDefault("canUpgradeGems", true);
        config.setDefault("canCraftGems", true);
        config.setDefault("keepGemsOnDeath", true);
        config.setDefault("gemsHaveDescriptions", false);
        config.setDefault("explosionDamageAllowed", true);
        config.setDefault("preventGemPowerTampering", true);
        config.setDefault("doGemDecay", true);
        config.setDefault("doDecayOnLevel1", false);
        config.setDefault("dragonEggHalfCooldown", true);
        config.setDefault("randomizedColors", false);
        config.setDefault("cooldownBoostPerLevelInSeconds", 2L);
        config.setDefault("delayToUseGemsOnJoin", 30);
        config.setDefault("gemCreationAttempts", 10);
        config.setDefault("blockedLavaBlocks", new Material[]{Material.BEDROCK, Material.WATER, Material.NETHERITE_BLOCK});
    }

    public long getGemCooldownBoost(){
        return config.getLong("cooldownBoostPerLevelInSeconds");
    }

    public boolean isDragonEggHalfCooldown() {
        return config.getBoolean("dragonEggHalfCooldown");
    }

    public boolean isGemActive(String name) {
        return activeGems.getOrSetDefault(name+"GemActive", true);
    }

    public int getStartingCooldown(String name) {
        return cooldowns.getOrSetDefault(name+"Cooldown", 60);
    }

    public boolean getGiveGemOnFirstLogin() {
        return config.getBoolean("giveGemOnFirstLogin");
    }

    public long getDelayToUseGems() {
        return config.getLong("delayToUseGemsOnJoin");
    }

    public boolean isRandomizedColors() {
        return config.getBoolean("randomizedColors");
    }

    public boolean allowOnlyOneGem() {
        return config.getBoolean("allowOnlyOneGem");
    }

    public int getGemCreationAttempts() {
        return config.getInt("gemCreationAttempts");
    }
}
