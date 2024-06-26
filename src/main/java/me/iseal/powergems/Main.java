package me.iseal.powergems;

import de.leonhard.storage.Yaml;
import me.iseal.powergems.commands.*;
import me.iseal.powergems.gems.powerClasses.tasks.WaterGemPassive;
import me.iseal.powergems.listeners.*;
import me.iseal.powergems.listeners.passivePowerListeners.DamageListener;
import me.iseal.powergems.listeners.powerListeners.*;
import me.iseal.powergems.managers.*;
import me.iseal.powergems.tasks.AddCooldownToToolBar;
import me.iseal.powergems.tasks.CheckMultipleEmeraldsTask;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    private static JavaPlugin plugin = null;
    public static Yaml cd = null;
    public static Yaml gemActive = null;
    public static Yaml config = null;
    private static SingletonManager sm = null;
    private static NamespacedKey isGemKey = null;
    private static NamespacedKey gemPowerKey = null;
    private static NamespacedKey gemLevelKey = null;
    private static NamespacedKey isGemProjectileKey = null;
    private static NamespacedKey isRandomGemKey = null;
    private static NamespacedKey isGemExplosionKey = null;
    private static final UUID attributeUUID = UUID.fromString("d21d674e-e7ec-4cd0-8258-4667843f26fd");
    private final Logger l = Bukkit.getLogger();

    @Override
    public void onEnable() {
        l.info("Initializing plugin");
        plugin = this;
        sm = SingletonManager.getInstance();
        sm.init();
        cd = new Yaml("cooldowns", this.getDataFolder() + "\\config\\");
        gemActive = new Yaml("gem_active", this.getDataFolder() + "\\config\\");
        isGemKey = new NamespacedKey(this, "is_power_gem");
        gemPowerKey = new NamespacedKey(this, "gem_power");
        gemLevelKey = new NamespacedKey(this, "gem_level");
        isGemProjectileKey = new NamespacedKey(this, "is_gem_projectile");
        isRandomGemKey = new NamespacedKey(this, "is_random_gem");
        isGemExplosionKey = new NamespacedKey(this, "is_gem_explosion");
        sm.initLater();
        new AddCooldownToToolBar().runTaskTimer(this, 0, 20);
        if (sm.configManager.getGeneralConfigManager().allowOnlyOneGem())
            new CheckMultipleEmeraldsTask().runTaskTimer(this, 100, 60);
        l.info("Registering listeners");
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new UseEvent(), this);
        pluginManager.registerEvents(new EnterExitListener(), this);
        if (config.getBoolean("keepGemsOnDeath"))
            pluginManager.registerEvents(new DeathEvent(), this);
        if (!config.getBoolean("canDropGems"))
            pluginManager.registerEvents(new DropEvent(), this);
        if (!config.getBoolean("explosionDamageAllowed"))
            pluginManager.registerEvents(new EntityExplodeListener(), this);
        if (config.getBoolean("preventGemPowerTampering"))
            pluginManager.registerEvents(new NoGemHittingListener(), this);
        // if (!config.getBoolean("allowMovingGems")) pluginManager.registerEvents(new
        // IInventoryMoveEvent(), this);
        pluginManager.registerEvents(new IronProjectileLandListener(), this);
        pluginManager.registerEvents(new InventoryCloseListener(), this);
        pluginManager.registerEvents(new DamageListener(), this);
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
        l.info("Registering tasks");
        new WaterGemPassive().runTaskTimer(this, 0, 15);
        l.info("Registered tasks");
        if (sm.configManager.getGeneralConfigManager().isAllowBStatsMetrics()) {
            l.info("Registering bstats metrics");
            @SuppressWarnings("unused")
            Metrics metrics = new Metrics(plugin, 20723);
        }
        //TODO: addon api
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // getters beyond this point
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static NamespacedKey getIsGemKey() {
        return isGemKey;
    }

    public static NamespacedKey getGemPowerKey() {
        return gemPowerKey;
    }

    public static NamespacedKey getGemLevelKey() {
        return gemLevelKey;
    }

    public static NamespacedKey getIsGemProjectileKey() {
        return isGemProjectileKey;
    }

    public static NamespacedKey getIsRandomGemKey() {
        return isRandomGemKey;
    }

    public static NamespacedKey getIsGemExplosionKey() {
        return isGemExplosionKey;
    }

    public static UUID getAttributeUUID() {
        return attributeUUID;
    }

}