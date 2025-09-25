package dev.iseal.powergems.listeners;

import dev.iseal.ExtraKryoCodecs.Enums.SerializersEnums.AnalyticsAPI.PowerGemsAnalyticsSerializers;
import dev.iseal.ExtraKryoCodecs.Holders.AnalyticsAPI.PowerGems.PGAddonsLoaded;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.api.ApiManager;
import dev.iseal.powergems.managers.Addons.AddonsManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealUtils.systems.analytics.AnalyticsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class ServerStartupListener implements Listener {

    @EventHandler
    public void onStartup(ServerLoadEvent event) {
        SingletonManager.getInstance().initLater();

        //stop accepting gems
        ApiManager.getInstance().acceptsGems = false;

        // plugin addons init
        AddonsManager.INSTANCE.loadAddons();

        // send addons loaded to analytics
        GeneralConfigManager gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        if (gcm.isAllowMetrics()) {
            String aaid = gcm.getAnalyticsID();
            List<JavaPlugin> addons = ApiManager.getInstance().getGemPlugins();
            HashMap<String, String> addonsMap = new HashMap<>();
            for (JavaPlugin addon : addons) {
                String addonName = addon.getName();
                String addonVersion = addon.getDescription().getVersion();
                addonsMap.put(addonName, addonVersion);
            }
            PGAddonsLoaded addonsLoaded = new PGAddonsLoaded(addonsMap);
            AnalyticsManager.INSTANCE.sendEvent(
                    aaid,
                    PowerGemsAnalyticsSerializers.ADDONS_LOADED,
                    addonsLoaded
            );
            PowerGems.handleConfigChecksums(gcm.getAnalyticsID());
        }
    }
}
