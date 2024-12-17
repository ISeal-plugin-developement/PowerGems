package dev.iseal.powergems;

import com.google.gson.Gson;
import com.sk89q.worldguard.WorldGuard;
import de.leonhard.storage.Yaml;
import dev.iseal.powergems.commands.*;
import dev.iseal.powergems.listeners.*;
import dev.iseal.powergems.listeners.passivePowerListeners.DamageListener;
import dev.iseal.powergems.listeners.passivePowerListeners.WaterMoveListener;
import dev.iseal.powergems.listeners.powerListeners.IronProjectileLandListener;
import dev.iseal.powergems.managers.Addons.WorldGuard.WorldGuardAddonManager;
import dev.iseal.powergems.managers.Configuration.CooldownConfigManager;
import dev.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.WrapperObjects.GemUsageInfo;
import dev.iseal.powergems.tasks.AddCooldownToToolBar;
import dev.iseal.powergems.tasks.CheckMultipleEmeraldsTask;
import dev.iseal.powergems.tasks.CosmeticParticleEffect;
import dev.iseal.sealLib.I18N.I18N;
import dev.iseal.sealLib.Metrics.ConnectionManager;
import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PowerGems extends JavaPlugin {

    private static JavaPlugin plugin = null;
    public static Yaml config = null;
    public static boolean isWorldGuardEnabled = false;
    private static SingletonManager sm = null;
    private static final UUID attributeUUID = UUID.fromString("d21d674e-e7ec-4cd0-8258-4667843f26fd");
    private final Logger l = Bukkit.getLogger();
    private final HashMap<UUID, ArrayList<GemUsageInfo>> gemLevelDistributionData = new HashMap<>();

    @Override
    public void onEnable() {
        l.info("[PowerGems] Initializing plugin");
        plugin = this;
        sm = SingletonManager.getInstance();
        sm.init();
        if (!getDataFolder().exists())
            l.warning("[PowerGems] Generating configuration, this WILL spam the console.");
        firstSetup();
        GeneralConfigManager gcm = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        l.info("[PowerGems] -----------------------------------------------------------------------------------------");
        l.info("[PowerGems] PowerGems v" + getDescription().getVersion());
        l.info("[PowerGems] Made by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "").replace(",", " &"));
        l.info("[PowerGems] Loading in " + gcm.getLanguageCode() + "_" + gcm.getCountryCode() + " locale");
        l.info("[PowerGems] Loading server version: " + Bukkit.getServer().getVersion());
        l.info("[PowerGems] For info and to interact with the plugin, visit: "+ ConnectionManager.getInstance().sendDataToAPI("discord", "", "GET", false));
        l.info("[PowerGems] -----------------------------------------------------------------------------------------");
        try {
            I18N.getInstance().setBundle(this, gcm.getLanguageCode(), gcm.getCountryCode());
        } catch (IOException e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "FAILED_SET_BUNDLE");
        }
        new AddCooldownToToolBar().runTaskTimer(this, 0, 20);
        if (gcm.allowOnlyOneGem())
            new CheckMultipleEmeraldsTask().runTaskTimer(this, 100, 60);
        if (gcm.allowCosmeticParticleEffects())
            new CosmeticParticleEffect().runTaskTimer(this, 0, gcm.cosmeticParticleEffectInterval());
        l.info("[PowerGems] "+I18N.translate("REGISTERING_LISTENERS"));
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new UseEvent(), this);
        pluginManager.registerEvents(new EnterExitListener(), this);
        if (gcm.doKeepGemsOnDeath())
            pluginManager.registerEvents(new DeathEvent(), this);
        if (!gcm.canDropGems())
            pluginManager.registerEvents(new DropEvent(), this);
        if (!gcm.isExplosionDamageAllowed())
            pluginManager.registerEvents(new EntityExplodeListener(), this);
        if (gcm.doGemPowerTampering())
            pluginManager.registerEvents(new NoGemHittingListener(), this);
        // if (!config.getBoolean("allowMovingGems")) pluginManager.registerEvents(new
        // IInventoryMoveEvent(), this);
        pluginManager.registerEvents(new IronProjectileLandListener(), this);
        pluginManager.registerEvents(new InventoryCloseListener(), this);
        pluginManager.registerEvents(new DamageListener(), this);
        pluginManager.registerEvents(new WaterMoveListener(), this);
        pluginManager.registerEvents(new ServerLoadListener(), this);
        pluginManager.registerEvents(sm.strenghtMoveListen, this);
        pluginManager.registerEvents(sm.sandMoveListen, this);
        pluginManager.registerEvents(sm.recipeManager, this);
        l.info("[PowerGems] "+I18N.translate("REGISTERED_LISTENERS"));
        l.info("[PowerGems] "+I18N.translate("REGISTERING_COMMANDS"));
        Bukkit.getServer().getPluginCommand("givegem").setExecutor(new GiveGemCommand());
        Bukkit.getServer().getPluginCommand("giveallgem").setExecutor(new GiveAllGemCommand());
        Bukkit.getServer().getPluginCommand("checkupdates").setExecutor(new CheckUpdateCommand());
        Bukkit.getServer().getPluginCommand("reloadconfig").setExecutor(new ReloadConfigCommand());
        Bukkit.getServer().getPluginCommand("debug").setExecutor(new DebugCommand());
        l.info("[PowerGems] "+I18N.translate("REGISTERED_COMMANDS"));
        if (isWorldGuardEnabled() && gcm.isWorldGuardEnabled())
            WorldGuardAddonManager.getInstance().init();
        if (gcm.isAllowMetrics()) {
            sm.metricsManager = MetricsManager.getInstance();
            l.info("[PowerGems] "+I18N.translate("REGISTERING_METRICS"));
            sm.metricsManager = MetricsManager.getInstance();
            sm.metricsManager.addMetrics(PowerGems.getPlugin(), 20723);
            sm.metricsManager.addJoinMetrics(this::registerPlayerInfo);
            sm.metricsManager.addQuitMetrics(this::registerPlayerInfo);
            sm.metricsManager.addShutdownMetrics((player -> {
                Map<String, Map<String, Integer>> map = new HashMap<>();
                gemLevelDistributionData.forEach((uuid, gemUsageInfos) -> {
                    gemUsageInfos.forEach(gemUsageInfo -> {
                        String gemName = gemUsageInfo.getName();
                        int level = gemUsageInfo.getLevel();
                        Map<String, Integer> levelMap = map.getOrDefault(gemName, new HashMap<>());
                        levelMap.put(String.valueOf(level), levelMap.getOrDefault(String.valueOf(level), 0) + 1);
                        map.put(gemName, levelMap);
                    });
                });
                if (!map.isEmpty()) {
                    Gson gson = new Gson();
                    sm.metricsManager.addInfoToSendOnExit("powergems/gemlevelusage", gson.toJson(map));
                }
            }));
        }
        pluginManager.registerEvents(sm.metricsManager, this);
        l.info("[PowerGems] "+I18N.translate("INITIALIZED_PLUGIN"));
    }

    @Override
    public void onDisable() {
        if (sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class).isAllowMetrics())
            sm.metricsManager.exitAndSendInfo();
        getLogger().info("[PowerGems] Shutting down!");
    }

    // getters beyond this point
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    private void firstSetup() {
        if (getDataFolder().exists()) {
            return;
        }
        getDataFolder().mkdir();
        GemMaterialConfigManager gemMaterialConfigManager = sm.configManager.getRegisteredConfigInstance(GemMaterialConfigManager.class);
        CooldownConfigManager cooldownConfigManager = sm.configManager.getRegisteredConfigInstance(CooldownConfigManager.class);
        GemManager gemManager = sm.gemManager;
        gemManager.getAllGems().forEach((index, gem) -> {
            gemMaterialConfigManager.getGemMaterial(gem);
            cooldownConfigManager.getStartingCooldown(gemManager.getGemName(gem), "Right");
            cooldownConfigManager.getStartingCooldown(gemManager.getGemName(gem), "Left");
            cooldownConfigManager.getStartingCooldown(gemManager.getGemName(gem), "Shift");
        });
        l.warning("Finished generating configuration");
    }

    public static UUID getAttributeUUID() {
        return attributeUUID;
    }

    public boolean isWorldGuardEnabled() {
        try {
            WorldGuard.getInstance();
            isWorldGuardEnabled = true;
        } catch (NoClassDefFoundError e) {
            isWorldGuardEnabled = false;
        }
        return isWorldGuardEnabled;
    }

    private void registerPlayerInfo(Player plr){
        UUID playerUUID = plr.getUniqueId();
        if (gemLevelDistributionData.containsKey(playerUUID)) {
            gemLevelDistributionData.remove(playerUUID);
        }

        ArrayList<GemUsageInfo> usageInfos = new ArrayList<>();
        sm.utils.getUserGems(plr).forEach(gem -> {
            usageInfos.add(new GemUsageInfo(sm.gemManager.getGemName(gem), sm.gemManager.getLevel(gem)));
        });

        gemLevelDistributionData.put(playerUUID, usageInfos);
    }
}