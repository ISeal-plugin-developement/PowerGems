package dev.iseal.powergems;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.WorldGuard;

import de.leonhard.storage.Yaml;
import dev.iseal.powergems.commands.CheckUpdateCommand;
import dev.iseal.powergems.commands.DebugCommand;
import dev.iseal.powergems.commands.GetAllGemsCommand;
import dev.iseal.powergems.commands.GiveAllGemCommand;
import dev.iseal.powergems.commands.GiveGemCommand;
import dev.iseal.powergems.commands.ReloadConfigCommand;
import dev.iseal.powergems.listeners.AvoidTargetListener;
import dev.iseal.powergems.listeners.CraftEventListener;
import dev.iseal.powergems.listeners.DeathEvent;
import dev.iseal.powergems.listeners.DropEvent;
import dev.iseal.powergems.listeners.EnterExitListener;
import dev.iseal.powergems.listeners.EntityExplodeListener;
import dev.iseal.powergems.listeners.InventoryCloseListener;
import dev.iseal.powergems.listeners.NoGemHittingListener;
import dev.iseal.powergems.listeners.ServerLoadListener;
import dev.iseal.powergems.listeners.SnowballDamageListener;
import dev.iseal.powergems.listeners.TradeEventListener;
import dev.iseal.powergems.listeners.UseEvent;
import dev.iseal.powergems.listeners.passivePowerListeners.DamageListener;
import dev.iseal.powergems.listeners.passivePowerListeners.DebuffInColdBiomesListener;
import dev.iseal.powergems.listeners.passivePowerListeners.DebuffInHotBiomesListener;
import dev.iseal.powergems.listeners.passivePowerListeners.FirePermaEffect;
import dev.iseal.powergems.listeners.passivePowerListeners.StrengthPermaEffect;
import dev.iseal.powergems.listeners.passivePowerListeners.WaterMoveListener;
import dev.iseal.powergems.listeners.powerListeners.IronProjectileLandListener;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.Configuration.CooldownConfigManager;
import dev.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.tasks.AddCooldownToToolBar;
import dev.iseal.powergems.tasks.CheckMultipleEmeraldsTask;
import dev.iseal.powergems.tasks.CosmeticParticleEffect;
import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.sealLib.Utils.ExceptionHandler;

public class PowerGems extends JavaPlugin {

    private static JavaPlugin plugin = null;
    public static Yaml config = null;
    public static boolean isWorldGuardEnabled = false;
    private static SingletonManager sm = null;
    private static final UUID attributeUUID = UUID.fromString("d21d674e-e7ec-4cd0-8258-4667843f26fd");
    private final Logger l = this.getLogger();
    private boolean errorOnDependencies = false;
    private final HashMap<String, String> dependencies = new HashMap<>();
    {
        dependencies.put("SealLib", "1.1.0.0");
    }
    
    //private final HashMap<UUID, ArrayList<GemUsageInfo>> gemLevelDistributionData = new HashMap<>();

    @Override
    public void onEnable() {
        l.info("Initializing plugin");
        plugin = this;
        for (Map.Entry<String, String> entry : dependencies.entrySet()) {
            if (Bukkit.getPluginManager().getPlugin(entry.getKey()) == null) {
                l.severe("The plugin " + entry.getKey() + " is required for this plugin to work. Please install it.");
                l.severe("PowerGems will shut down now.");
                errorOnDependencies = true;
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            if (!Bukkit.getPluginManager().getPlugin(entry.getKey()).getDescription().getVersion().equals(entry.getValue())) {
                l.severe("The plugin " + entry.getKey() + " is using the wrong version! Please install version " + entry.getValue());
                l.severe("PowerGems will shut down now.");
                errorOnDependencies = true;
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        sm = SingletonManager.getInstance();
        sm.init();
        if (!getDataFolder().exists())
            l.warning("Generating configuration, this WILL spam the console.");
        firstSetup();
        GeneralConfigManager gcm = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        l.info("-----------------------------------------------------------------------------------------");
        l.info("PowerGems v" + getDescription().getVersion());
        l.info("Made by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "").replace(",", " &"));
        l.info("Loading in " + gcm.getLanguageCode() + "_" + gcm.getCountryCode() + " locale");
        l.info("Loading server version: " + Bukkit.getServer().getVersion());
        l.info("For info and to interact with the plugin, visit: https://discord.iseal.dev/");
        l.info("-----------------------------------------------------------------------------------------");
        try {
            I18N.getInstance().setBundle(this, gcm.getLanguageCode(), gcm.getCountryCode());
        } catch (IOException e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "FAILED_SET_BUNDLE");
        }
        
        // Replace BukkitRunnable tasks with Paper's modern scheduler API
        this.getServer().getScheduler().runTaskTimer(this, task -> {
            new AddCooldownToToolBar().run();
        }, 0L, 20L);

        if (gcm.allowOnlyOneGem()) {
            CheckMultipleEmeraldsTask.schedule(this);
        }

        if (gcm.allowCosmeticParticleEffects()) {
            this.getServer().getScheduler().runTaskTimer(this, task -> {
                new CosmeticParticleEffect().run();
            }, 0L, gcm.cosmeticParticleEffectInterval());
        }
        new StrengthPermaEffect();
        new FirePermaEffect(); 
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
        pluginManager.registerEvents(AvoidTargetListener.getInstance(), this);
        if (gcm.doDebuffForTemperature()) {
            pluginManager.registerEvents(new DebuffInColdBiomesListener(), this);
            pluginManager.registerEvents(new DebuffInHotBiomesListener(), this);
        }
        pluginManager.registerEvents(new SnowballDamageListener(), this);
        pluginManager.registerEvents(new IronProjectileLandListener(), this);
        pluginManager.registerEvents(new InventoryCloseListener(), this);
        pluginManager.registerEvents(new DamageListener(), this);
        pluginManager.registerEvents(new WaterMoveListener(), this);
        pluginManager.registerEvents(new ServerLoadListener(), this);
        pluginManager.registerEvents(new TradeEventListener(), this);
        pluginManager.registerEvents(new CraftEventListener(), this);
        pluginManager.registerEvents(sm.strenghtMoveListen, this);
        pluginManager.registerEvents(sm.sandMoveListen, this);
        pluginManager.registerEvents(sm.recipeManager, this);
        l.info(I18N.translate("REGISTERED_LISTENERS"));
        l.info(I18N.translate("REGISTERING_COMMANDS"));
        Bukkit.getServer().getPluginCommand("givegem").setExecutor(new GiveGemCommand());
        Bukkit.getServer().getPluginCommand("giveallgem").setExecutor(new GiveAllGemCommand());
        Bukkit.getServer().getPluginCommand("checkupdates").setExecutor(new CheckUpdateCommand());
        Bukkit.getServer().getPluginCommand("reloadconfig").setExecutor(new ReloadConfigCommand());
        Bukkit.getServer().getPluginCommand("pgDebug").setExecutor(new DebugCommand());
        Bukkit.getServer().getPluginCommand("getallgems").setExecutor(new GetAllGemsCommand());
        l.info(I18N.translate("REGISTERED_COMMANDS"));
        if (isWorldGuardEnabled() && gcm.isWorldGuardEnabled())
            dev.iseal.powergems.managers.Addons.WorldGuard.WorldGuardAddonManager.getInstance().init();
        if (gcm.isAllowMetrics()) {
            sm.metricsManager = MetricsManager.getInstance();
            l.info(I18N.translate("REGISTERING_METRICS"));
            sm.metricsManager = MetricsManager.getInstance();
            sm.metricsManager.addMetrics(PowerGems.getPlugin(), 20723);
            /*
            //TODO: this needs a complete rework. disabled for now
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
             */
        }
        //pluginManager.registerEvents(sm.metricsManager, this);
        l.info(I18N.translate("INITIALIZED_PLUGIN"));
    }

    @Override
    public void onDisable() {
        /*
        //TODO: part of the metrics rework. disabled for now, needs a complete rework.
        if (!errorOnDependencies && sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class).isAllowMetrics())
            sm.metricsManager.exitAndSendInfo();
         */
        getLogger().info("Shutting down!");
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
            cooldownConfigManager.getStartingCooldown(gemManager.getName(gem), "Right");
            cooldownConfigManager.getStartingCooldown(gemManager.getName(gem), "Left");
            cooldownConfigManager.getStartingCooldown(gemManager.getName(gem), "Shift");
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

    /*
    //TODO: part of the metrics rework. disabled for now, needs a complete rework.
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
     */
}