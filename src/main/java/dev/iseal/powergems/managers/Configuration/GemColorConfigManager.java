package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.powergems.misc.Interfaces.Dumpable;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class GemColorConfigManager extends AbstractConfigManager implements Dumpable {
    public GemColorConfigManager() {
        super("GemColors");
    }

    @Override
    public void setUpConfig() {

    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            file.setDefault(GemManager.lookUpName(i) + "GemColor", ChatColor.GREEN.name());
        }
    }

    public ChatColor getGemColor(String gemName) {
        return ChatColor.valueOf(file.getOrSetDefault(gemName+"GemColor", ChatColor.GREEN.name()));
    }


    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> map = new HashMap<>();
        file.keySet().forEach(key -> map.put(key, file.get(key)));
        return map;
    }
}
