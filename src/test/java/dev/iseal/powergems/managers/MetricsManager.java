package dev.iseal.powergems.managers;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.misc.GemUsageInfo;
import dev.iseal.powergems.misc.Utils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
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

public class MetricsManager implements Listener {

    public Metrics metrics;
    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final HashMap<UUID, ArrayList<GemUsageInfo>> gemLevelDistributionData = new HashMap<>();

    public void init() {
        metrics = new Metrics(PowerGems.getPlugin(), 20723);
    }

    public void exitAndSendInfo() {
        Bukkit.getServer().getOnlinePlayers().forEach(this::registerPlayerInfo);

        metrics.addCustomChart(new DrilldownPie("gem_level_distribution", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            ArrayList<String> gems = new ArrayList<>();
            Map<String, Integer> levels = new HashMap<>();
            gemLevelDistributionData.forEach((uuid, gemUsageInfos) -> {
                gemUsageInfos.forEach(gemUsageInfo -> {
                    String gemName = gemUsageInfo.getName();
                    int level = gemUsageInfo.getLevel();

                    // only add the type once
                    if (!gems.contains(gemName)) {
                        gems.add(gemName);
                    }

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
            return map;
        }));
        
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
