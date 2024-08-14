package dev.iseal.powergems.managers.Metrics;

import com.google.gson.Gson;
import de.leonhard.storage.Json;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.ExceptionHandler;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.powergems.misc.WrapperObjects.GemUsageInfo;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class MetricsManager implements Listener {

    public Metrics metrics;
    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final HashMap<UUID, ArrayList<GemUsageInfo>> gemLevelDistributionData = new HashMap<>();
    private final Json metricsFile = new Json("metrics", PowerGems.getPlugin().getDataFolder()+"\\data\\");

    public void init() {
        metrics = new Metrics(PowerGems.getPlugin(), 20723);
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

        if (!map.isEmpty()) {
            Gson gson = new Gson();
            ConnectionManager.getInstance().sendData("powergems/gemlevelusage", gson.toJson(map));
        }

        if(ExceptionHandler.getInstance().hasErrors){
            List<String> errors = ExceptionHandler.getInstance().errorMessages;
            Gson gson = new Gson();
            ConnectionManager.getInstance().sendData("powergems/errorcodes", gson.toJson(errors));
            metricsFile.remove("error_messages");
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
