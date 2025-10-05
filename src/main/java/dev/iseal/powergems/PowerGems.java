package dev.iseal.powergems;

import dev.iseal.ExtraKryoCodecs.Enums.SerializersEnums.AnalyticsAPI.AnalyticsSerializers;
import dev.iseal.ExtraKryoCodecs.Holders.AnalyticsAPI.PluginVersionInfo;
import dev.iseal.powergems.commands.*;
import dev.iseal.powergems.gems.powerClasses.tasks.IceGemGolemAi;
import dev.iseal.powergems.listeners.*;
import dev.iseal.powergems.listeners.passivePowerListeners.DamageListener;
import dev.iseal.powergems.listeners.passivePowerListeners.DebuffInColdBiomesListener;
import dev.iseal.powergems.listeners.passivePowerListeners.DebuffInHotBiomesListener;
import dev.iseal.powergems.listeners.passivePowerListeners.WaterMoveListener;
import dev.iseal.powergems.listeners.powerListeners.IronProjectileLandListener;
import dev.iseal.powergems.managers.Addons.AddonLoadOrder;
import dev.iseal.powergems.managers.Addons.AddonsManager;
import dev.iseal.powergems.managers.Configuration.CooldownConfigManager;
import dev.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.powergems.tasks.AddCooldownToToolBar;
import dev.iseal.powergems.tasks.CheckMultipleGemsTask;
import dev.iseal.powergems.tasks.CosmeticParticleEffect;
import dev.iseal.powergems.tasks.PermanentEffectsGiverTask;
import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.sealUtils.SealUtils;
import dev.iseal.sealUtils.systems.analytics.AnalyticsManager;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PowerGems extends JavaPlugin {

    private static JavaPlugin plugin = null;
    private static SingletonManager sm = null;
    private static final UUID attributeUUID = UUID.fromString("d21d674e-e7ec-4cd0-8258-4667843f26fd");
    private final Logger log = this.getLogger();
    private final HashMap<String, String> dependencies = new HashMap<>();
    {
        dependencies.put("SealLib", "1.2.0.1"); //NOPMD - This is not an IP.
    }

    @Override
    public void onEnable() {
        log.info("Initializing plugin");
        plugin = this;

        if (System.getenv().containsKey("POWERGEMS_DISABLE_DEPENDENCY_CHECK") && System.getenv("POWERGEMS_DISABLE_DEPENDENCY_CHECK").equalsIgnoreCase("true")) {
            log.warning("Ignoring dependency check due to environment variable.");
        } else {
            Map<String, String> missingDeps = checkHardDependencies();
            if (!missingDeps.isEmpty()) {
                // register join listener to warn admins
                Bukkit.getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onJoin(PlayerJoinEvent event) {
                        Player player = event.getPlayer();
                        if (player.isOp() || player.hasPermission("powergems.admin")) {
                            player.sendMessage("§cPowerGems might not be operating correctly due to missing dependencies. Please check the console for details.");
                            player.sendMessage("§cMissing dependencies:");
                            for (Map.Entry<String, String> entry : missingDeps.entrySet()) {
                                player.sendMessage("§c- " + entry.getKey() + " (required version: " + entry.getValue() + ")");
                            }
                        }
                    }
                }, this);
            } else {
                log.info("All hard dependencies are satisfied.");
            }
        }

        ExceptionHandler.getInstance().setVersion(plugin.getDescription().getVersion());
        sm = SingletonManager.getInstance();
        sm.init();
        if (!getDataFolder().exists())
            log.warning("Generating configuration, this WILL spam the console.");
        firstSetup();
        GeneralConfigManager gcm = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        log.info("-----------------------------------------------------------------------------------------");
        log.info("PowerGems v" + getDescription().getVersion());
        log.info("Made by " + getDescription().getAuthors().toString().replace("[", "").replace("]", "").replace(",", " &"));
        log.info("Loading in " + gcm.getLanguageCode() + "_" + gcm.getCountryCode() + " locale");
        log.info("Loading server version: " + Bukkit.getServer().getVersion());
        log.info("For info and to interact with the plugin, visit: https://discord.iseal.dev/");
        log.info("-----------------------------------------------------------------------------------------");
        try {
            I18N.getInstance().setBundle(this, gcm.getLanguageCode(), gcm.getCountryCode());
        } catch (IOException e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "FAILED_SET_BUNDLE");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        new AddCooldownToToolBar().runTaskTimer(this, 0, 20);

        if (gcm.allowOnlyOneGem())
            new CheckMultipleGemsTask().runTaskTimer(this, 100L, 20L);


        if (gcm.allowCosmeticParticleEffects())
            new CosmeticParticleEffect().runTaskTimer(this, 0L, gcm.cosmeticParticleEffectInterval());

        if(gcm.giveGemPermanentEffectOnLvlX())
            new PermanentEffectsGiverTask().runTaskTimer(this, 100L, 80L);

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
        if (!gcm.isAllowMovingGems())
            pluginManager.registerEvents(new InventoryMoveEvent(), this);
        pluginManager.registerEvents(AvoidTargetListener.getInstance(), this);
        if (gcm.doDebuffForTemperature()) {
            pluginManager.registerEvents(new DebuffInColdBiomesListener(), this);
            pluginManager.registerEvents(new DebuffInHotBiomesListener(), this);
        }
        if(gcm.upgradeGemOnKill()) {
            pluginManager.registerEvents(new KillEventListener(), this);
        }
        pluginManager.registerEvents(new IceGemGolemAi(), this);
        pluginManager.registerEvents(new IronProjectileLandListener(), this);
        pluginManager.registerEvents(new InventoryCloseListener(), this);
        pluginManager.registerEvents(new DamageListener(), this);
        pluginManager.registerEvents(new WaterMoveListener(), this);
        pluginManager.registerEvents(new ServerStartupListener(), this);
        pluginManager.registerEvents(new TradeEventListener(), this);
        pluginManager.registerEvents(new CraftEventListener(), this);
        pluginManager.registerEvents(sm.strenghtMoveListener, this);
        pluginManager.registerEvents(sm.sandMoveListen, this);
        pluginManager.registerEvents(sm.recipeManager, this);
        pluginManager.registerEvents(new EntityDamageListener(), this);
        pluginManager.registerEvents(new BundleListener(), this);
        log.info(I18N.translate("REGISTERED_LISTENERS"));
        log.info(I18N.translate("REGISTERING_COMMANDS"));
        Bukkit.getServer().getPluginCommand("givegem").setExecutor(new GiveGemCommand());
        Bukkit.getServer().getPluginCommand("giveallplayersgem").setExecutor(new GiveAllPlayersGemCommand());
        Bukkit.getServer().getPluginCommand("checkupdates").setExecutor(new CheckUpdateCommand());
        Bukkit.getServer().getPluginCommand("reloadconfig").setExecutor(new ReloadConfigCommand());
        Bukkit.getServer().getPluginCommand("pgDebug").setExecutor(new DebugCommand());
        Bukkit.getServer().getPluginCommand("getallgems").setExecutor(new GetAllGemsCommand());
        log.info(I18N.translate("REGISTERED_COMMANDS"));

        // AddonsManager.INSTANCE.loadAddons(); - currently moved to ServerStartupListener, just throwing shit at the wall

        AnalyticsManager.INSTANCE.setEnabled("PowerGems", false);
        ExceptionHandler.getInstance().setVersion(plugin.getDescription().getVersion());
        // plugin addons init
        AddonsManager.INSTANCE.initAddons(AddonLoadOrder.ON_ENABLE);

        if (gcm.isAllowMetrics()) {
            sm.metricsManager = MetricsManager.getInstance();
            log.info(I18N.translate("REGISTERING_METRICS"));
            sm.metricsManager = MetricsManager.getInstance();
            sm.metricsManager.addMetrics(PowerGems.getPlugin(), 20723);
            AnalyticsManager.INSTANCE.sendEvent(
                    gcm.getAnalyticsID(),
                    AnalyticsSerializers.PLUGIN_VERSION_INFO,
                    new PluginVersionInfo(
                            plugin.getDescription().getVersion(), // pluginVersion
                            Bukkit.getBukkitVersion().split("-")[0], // MC version
                            Bukkit.getServer().getVersion(), // serverVersion
                            Bukkit.getServer().getName(), // serverSoftware
                            System.getProperty("java.version"), // serverJavaVersion
                            System.getProperty("os.name"), // serverOS
                            System.getProperty("os.version"), // serverOSVersion
                            System.getProperty("os.arch") // serverArchitecture
                    )
            );

        }
        log.info(I18N.translate("INITIALIZED_PLUGIN"));
    }

    @Override
    public void onDisable() {
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
        log.warning("Finished generating configuration");
    }

    public static UUID getAttributeUUID() {
        return attributeUUID;
    }

    public static boolean isEnabled(String pluginName) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        return Arrays.stream(pluginManager.getPlugins()).anyMatch(plugin1 -> plugin1.getName().equals(pluginName));
    }

    private Map<String, String> checkHardDependencies() {
        HashMap<String, String> missingHardDependencies = new HashMap<>();
        for (Map.Entry<String, String> entry : dependencies.entrySet()) {
            if (Bukkit.getPluginManager().getPlugin(entry.getKey()) == null) {
                log.severe("The plugin " + entry.getKey() + " (version "+entry.getValue()+") is required for this plugin to work. Please install it.");
                missingHardDependencies.put(entry.getKey(), entry.getValue());
            }
            if (!Bukkit.getPluginManager().getPlugin(entry.getKey()).getDescription().getVersion().equals(entry.getValue())) {
                log.severe("The plugin " + entry.getKey() + " is using the wrong version! Please install version " + entry.getValue());
                missingHardDependencies.put(entry.getKey(), entry.getValue());
            }
        }
        return missingHardDependencies;
    }

    public static void handleConfigChecksums(String analyticsID) {
        File configFolder = PowerGems.getPlugin().getDataFolder();
        TempDataManager manager = SingletonManager.getInstance().tempDataManager;
        String storedChecksum = manager.readDataFromFile("configChecksum") instanceof String ? (String) manager.readDataFromFile("configChecksum") : null;
        GeneralConfigManager gcm = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class);

        String currentChecksum = calculateFolderChecksum(configFolder);
        if (storedChecksum == null) {
            manager.writeDataToFile("configChecksum", currentChecksum);
        } else if (gcm.isAllowMetrics()) {
            new Thread(() -> {
                try {
                    HttpResponse<String> res = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(30L))
                            .version(HttpClient.Version.HTTP_2)
                            .build()
                            .send(
                                    HttpRequest.newBuilder()
                                            .POST(HttpRequest.BodyPublishers.ofString(storedChecksum.equals(currentChecksum) ? "false" : "true"))
                                            .header("AA-ID", analyticsID)
                                            .uri(new URI((SealUtils.isDebug() ? "http://localhost:8080/" : "https://analytics.iseal.dev/") + "api/v2/PowerGems/config_changed"))
                                            .build(),
                                    HttpResponse.BodyHandlers.ofString()
                            );
                    if (res.statusCode() == 200) {
                        PowerGems.getPlugin().getLogger().info("Config checksum event sent successfully");
                    } else if (res.statusCode() == 429) {
                        PowerGems.getPlugin().getLogger().info("You are currently being rate-limited by the server. Any \"Rate limit\" or \"Status code 429\" errors can be safely ignored.");
                    } else {
                        PowerGems.getPlugin().getLogger().warning("Failed to send config checksum event, status code: " + res.statusCode());
                    }
                } catch (Exception e) {
                    ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "FAILED_TO_SEND_CONFIG_CHECKSUM_EVENT", false);
                }
            }, "Analytics-Sender-PowerGems").start();
        }
    }

    private static String calculateFolderChecksum(File folder) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            Path dataFolderPath = folder.toPath();
            Files.walk(dataFolderPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        Path relativePath = dataFolderPath.relativize(path);
                        return !relativePath.startsWith("data") && !relativePath.startsWith("languages");
                    })
                    .forEach(path -> {
                        try (InputStream is = Files.newInputStream(path)) {
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = is.read(buffer)) != -1) {
                                digest.update(buffer, 0, read);
                            }
                        } catch (IOException e) {
                            PowerGems.getPlugin().getLogger().warning("Error reading file: " + path);
                        }
                    });
        } catch (NoSuchAlgorithmException | IOException e) {
            PowerGems.getPlugin().getLogger().warning("Could not calculate checksum: " + e.getMessage());
            return null;
        }
        return bytesToHex(digest.digest());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}