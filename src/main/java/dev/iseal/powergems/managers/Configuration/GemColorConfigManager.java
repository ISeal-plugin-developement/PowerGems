package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.ConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GemColorConfigManager extends AbstractConfigManager {
    public GemColorConfigManager() {
        super("GemColors");
    }

    private final Logger logger = PowerGems.getPlugin().getLogger();
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private GeneralConfigManager gcm;

    @Override
    public void setUpConfig() {
        gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            file.setDefault(GemManager.lookUpName(i) + "GemColor", NamedTextColor.GREEN.asHexString());
        }
    }

    public Component getGemDisplayName(String gemName) {
        if (gcm.isRandomizedColors()) {
            return Component.text(gemName + " Gem").color(TextColor.color(ThreadLocalRandom.current().nextInt(0x1000000)));
        }
        String value = file.getOrSetDefault(gemName+"GemColor", NamedTextColor.GREEN.asHexString());
        return miniMessage.deserialize(value);
    }
}
