package dev.iseal.powergems;

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
import dev.iseal.powergems.managers.Metrics.MetricsManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.tasks.AddCooldownToToolBar;
import dev.iseal.powergems.tasks.CheckMultipleEmeraldsTask;
import dev.iseal.powergems.tasks.CosmeticParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public class PowerGems extends JavaPlugin {

    private static JavaPlugin plugin = null;
    public static Yaml config = null;
    public static boolean isWorldGuardEnabled = false;
    private static SingletonManager sm = null;
    private static final UUID attributeUUID = UUID.fromString("d21d674e-e7ec-4cd0-8258-4667843f26fd");
    private final Logger l = Bukkit.getLogger();

    @Override
    public void onEnable() {
        l.info("Initializing plugin");
        plugin = this;
        sm = SingletonManager.getInstance();
        sm.init();
        if (!getDataFolder().exists())
            l.warning("Generating configuration, this WILL spam the console.");
        firstSetup();
        GeneralConfigManager gcm = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        new AddCooldownToToolBar().runTaskTimer(this, 0, 20);
        if (gcm.allowOnlyOneGem())
            new CheckMultipleEmeraldsTask().runTaskTimer(this, 100, 60);
        if (gcm.allowCosmeticParticleEffects())
            new CosmeticParticleEffect().runTaskTimer(this, 0, gcm.cosmeticParticleEffectInterval());
        l.info("Registering listeners");
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
        pluginManager.registerEvents(sm.metricsManager, this);
        pluginManager.registerEvents(sm.strenghtMoveListen, this);
        pluginManager.registerEvents(sm.sandMoveListen, this);
        pluginManager.registerEvents(sm.recipeManager, this);
        l.info("Registered listeners");
        l.info("Registering commands");
        Bukkit.getServer().getPluginCommand("givegem").setExecutor(new GiveGemCommand());
        Bukkit.getServer().getPluginCommand("giveallgem").setExecutor(new GiveAllGemCommand());
        Bukkit.getServer().getPluginCommand("checkupdates").setExecutor(new CheckUpdateCommand());
        Bukkit.getServer().getPluginCommand("reloadconfig").setExecutor(new ReloadConfigCommand());
        Bukkit.getServer().getPluginCommand("debug").setExecutor(new DebugCommand());
        l.info("Registered commands");
        if (isWorldGuardEnabled() && gcm.isWorldGuardEnabled())
            WorldGuardAddonManager.getInstance().init();
        if (gcm.isAllowMetrics()) {
            l.info("Registering bstats metrics");
            MetricsManager metricsManager = sm.metricsManager;
            metricsManager.init();
        }
    }

    @Override
    public void onDisable() {
        if (sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class).isAllowMetrics())
            sm.metricsManager.exitAndSendInfo();
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
}