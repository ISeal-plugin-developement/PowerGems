package dev.iseal.powergems.managers.Metrics;

import com.google.gson.Gson;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.powergems.misc.WrapperObjects.GemUsageInfo;
import org.bstats.bukkit.Metrics;
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
            ConnectionManager.getInstance().sendData("powergems/gemlevelusage", gson.toJson(map));
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

    public void sendError(String errorMessage) {
        Gson gson = new Gson();
        ConnectionManager.getInstance().sendData("powergems/errorcodes", gson.toJson(errorMessage));
    }
}
