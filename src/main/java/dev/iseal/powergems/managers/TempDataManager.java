package dev.iseal.powergems.managers;

import de.leonhard.storage.Json;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.gems.powerClasses.tasks.FireballPowerDecay;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Logger;

public class TempDataManager {

    // Fire players in fireball
    public HashMap<Player, FireballPowerDecay> chargingFireball = new HashMap<>(1);

    // Players not able to use gems
    public HashMap<Player, Long> cantUseGems = new HashMap<>(1);

    // Iron shift players that left
    public LinkedList<UUID> ironShiftLeft = new LinkedList<>();

    // Iron right players that left
    public LinkedList<UUID> ironRightLeft = new LinkedList<>();

    private final Json tempData = new Json(PowerGems.getPlugin().getDataFolder()+"/data/", "data.json");
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
