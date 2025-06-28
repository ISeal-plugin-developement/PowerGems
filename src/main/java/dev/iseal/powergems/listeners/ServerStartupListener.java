package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerStartupListener implements Listener {

    @EventHandler
    public void onStartup(ServerLoadEvent event) {
        SingletonManager.getInstance().initLater();
    }
}
