package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import org.bukkit.ChatColor;

public class GemColorConfigManager extends AbstractConfigManager {
    public GemColorConfigManager() {
        super("GemColors");
    }

    @Override
    public void setUpConfig() {
        file.setHeader(
                "This config file manages the colors of the gems",
                "The default color is GREEN",
                "Change the colors to whatever you want"
        );

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
}
