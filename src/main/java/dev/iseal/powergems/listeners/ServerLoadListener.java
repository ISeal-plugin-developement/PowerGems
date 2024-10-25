package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerLoadListener implements Listener {

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        SingletonManager.getInstance().initLater();
    }
}
