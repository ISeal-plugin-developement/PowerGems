package dev.iseal.powergems.listeners;

import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerLoadListener implements Listener {

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        SingletonManager.getInstance().initLater();
        /* Logger l = Bukkit.getLogger();
        GemManager gemManager = SingletonManager.getInstance().gemManager;
        ArrayList<String> gemNames = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            l.info("Creating gem " + i);
            ItemStack itemStack = gemManager.createGem();
            if (itemStack == null) {
                l.warning("Failed to create gem " + i);
                break;
            }
            if (gemManager.getGemName(itemStack) == null) {
                l.warning("Failed to create gem " + i + " its a null gem");
                break;
            }
            if (Objects.equals(gemManager.getName(itemStack), "Error")) {
                l.warning("Failed to create gem " + i + " its an error gem");
                break;
            }
            if (!gemNames.contains(gemManager.getGemName(itemStack))) {
                gemNames.add(gemManager.getGemName(itemStack));
            }
        }
        l.info("Created " + gemNames.size() + " unique gems");
        l.info("The created gems are: " + gemNames.toString()); */
    }
}
