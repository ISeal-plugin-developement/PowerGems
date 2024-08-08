package me.iseal.powergems.managers;

import de.leonhard.storage.Json;
import jdk.jshell.spi.ExecutionControl;
import me.iseal.powergems.Main;
import me.iseal.powergems.misc.ExceptionHandler;
import me.iseal.powergems.misc.GemUsageInfo;
import me.iseal.powergems.misc.Utils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MetricsManager implements Listener {

    public Metrics metrics;
    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final HashMap<UUID, ArrayList<GemUsageInfo>> gemLevelDistributionData = new HashMap<>();
    private final Json metricsFile = new Json("metrics", Main.getPlugin().getDataFolder()+"\\data\\");

    public void init() {
        metrics = new Metrics(Main.getPlugin(), 20723);

        if (metricsFile.contains("gem_level_distribution")) {
            metrics.addCustomChart(new DrilldownPie("gem_level_distribution", () -> (Map<String, Map<String, Integer>>) metricsFile.getMap("gem_level_distribution")));
            metricsFile.remove("gem_level_distribution");
        }
        if(metricsFile.contains("crashed")){
            metrics.addCustomChart(new SimplePie("error_distribution", () -> metricsFile.getString("error_message")));
            metricsFile.remove("crashed");
            metricsFile.remove("error_message");
        }
    }

    public void exitAndSendInfo() {
        Bukkit.getServer().getOnlinePlayers().forEach(this::registerPlayerInfo);
            Map<String, Map<String, Integer>> map = new HashMap<>();
            ArrayList<String> gems = new ArrayList<>();
            Map<String, Integer> levels = new HashMap<>();
            gemLevelDistributionData.forEach((uuid, gemUsageInfos) -> {
                gemUsageInfos.forEach(gemUsageInfo -> {
                    String gemName = gemUsageInfo.getName();
                    int level = gemUsageInfo.getLevel();
                    // only add the type once
                    if (!gems.contains(gemName))
                        gems.add(gemName);

                    // prefer higher levels
                    if (levels.containsKey(gemName)) {
                        if (levels.get(gemName) < level) {
                            levels.put(gemName, level);
                        }
                    } else {
                        levels.put(gemName, level);
                    }
                });
                });

            gems.forEach(gem -> {
                if (map.containsKey(gem)) {
                    if (map.get(gem).containsKey(levels.get(gem).toString())) {
                        map.get(gem).put(levels.get(gem).toString(), map.get(gem).get(levels.get(gem).toString()) + 1);
                    } else {
                        map.get(gem).put(levels.get(gem).toString(), 1);
                    }
                } else {
                    map.put(gem, new HashMap<>());
                    map.get(gem).put(levels.get(gem).toString(), 1);
                }
            });

            System.out.println("Saving gem level distrib. map: "+map);
            metricsFile.set("gem_level_distribution", map);

            if(ExceptionHandler.getInstance().shuttingDown) {
                metricsFile.set("crashed", true);
                metricsFile.set("error_message", ExceptionHandler.getInstance().errorMessage);
            }

        metrics.shutdown();

    }


    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        registerPlayerInfo(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        registerPlayerInfo(e.getPlayer());
    }

    private void registerPlayerInfo(Player plr) {
        UUID playerUUID = plr.getUniqueId();
        if (gemLevelDistributionData.containsKey(playerUUID)) {
            gemLevelDistributionData.remove(playerUUID);
        }

        ArrayList<GemUsageInfo> usageInfos = new ArrayList<>();
        utils.getUserGems(plr).forEach(gem -> {
            usageInfos.add(new GemUsageInfo(gemManager.getGemName(gem), gemManager.getLevel(gem)));
        });

        gemLevelDistributionData.put(playerUUID, usageInfos);
    }

}
