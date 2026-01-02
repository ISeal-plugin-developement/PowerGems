package dev.iseal.powergems.managers;

import de.leonhard.storage.Json;
import dev.iseal.powergems.PowerGems;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Logger;

public class TempDataManager {

    // Players not able to use gems
    public HashMap<Player, Long> cantUseGems = new HashMap<>(1);

    // Iron shift players that left
    public LinkedList<UUID> ironShiftLeft = new LinkedList<>();

    // Iron right players that left
    public LinkedList<UUID> ironRightLeft = new LinkedList<>();

    private final Json tempData = new Json("data", PowerGems.getPlugin().getDataFolder()+"/data/");
    private final Logger log = PowerGems.getPlugin().getLogger();

    public Object readDataFromFile(String key) {
        if (tempData.contains(key)) {
            return tempData.get(key);
        } else {
            log.warning("Key '" + key + "' not found in temp data file.");
            return null;
        }
    }

    public void writeDataToFile(String key, Object value) {
        tempData.set(key, value);
        log.info("Data written to file: " + key + " = " + value);
    }
}
