package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.ConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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

    public static Component gradientText(String text, TextColor start, TextColor end) {
        if (text == null || text.isEmpty()) return Component.empty();

        // If 1 char, just color it with start (or end, same difference)
        if (text.length() == 1) {
            return Component.text(text).color(start);
        }

        Component result = Component.empty();
        int last = text.length() - 1;

        for (int i = 0; i < text.length(); i++) {
            float t = (float) i / (float) last; // 0..1
            TextColor step = TextColor.lerp(t, start, end);
            result = result.append(Component.text(String.valueOf(text.charAt(i))).color(step));
        }

        return result;
    }

    public Component getGemDisplayName(String gemName) {
        if (gcm.isRandomizedColors()) {
            return Component.text(gemName + " Gem").color(TextColor.color(ThreadLocalRandom.current().nextInt(0x1000000)));
        }
        String value = file.getOrSetDefault(gemName+"GemColor", NamedTextColor.GREEN.asHexString());
        if (value.contains("gradient")) {
            // gradient#color1#color2
            String colorOne = value.split("#")[1];
            String colorTwo = value.split("#")[2];
            try {
                return gradientText(gemName+ " Gem", TextColor.fromHexString(colorOne), TextColor.fromHexString(colorTwo));
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "[GemColorConfigManager] The gradient configuration for the gem "+gemName+" failed with message: "+e.getMessage());
                logger.log(Level.WARNING, "[GemColorConfigManager] The value was: "+value);
                logger.log(Level.WARNING, "[GemColorConfigManager] You probably misconfigured something. It's supposed to be gradient#color1#color2, where color1 and color2 are hex color codes. Example: gradient#FF0000#0000FF");
                logger.log(Level.WARNING, "[GemColorConfigManager] Falling back to default color.");
                return Component.text(gemName+" Gem").color(NamedTextColor.GREEN);
            }
        }
        return Component.text(gemName+" Gem").color(TextColor.fromHexString(value));
    }
}
