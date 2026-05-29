package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class GemNameConfigManager extends AbstractConfigManager {
    public GemNameConfigManager() {
        super("GemNames");
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
            file.setDefault(GemManager.lookUpName(i) + "GemName", "<green>"+GemManager.lookUpName(i)+" Gem");
        }
    }

    public Component getGemDisplayName(String gemName) {
        if (gcm.isRandomizedColors()) {
            return Component.text(gemName + " Gem")
                    .color(TextColor.color(ThreadLocalRandom.current().nextInt(0x1000000)))
                    .decoration(TextDecoration.ITALIC, false);
        }
        String value = file.getOrSetDefault(gemName+"GemName", "<red>Missing Gem Name");
        // Minecraft item display names are italic by default. If the configured MiniMessage
        // explicitly sets the italic decoration, respect it; otherwise clear italics so names
        // appear normal in item tooltips.
        Component parsed = miniMessage.deserialize(value);
        // If the parsed component explicitly has the italic decoration set, return as-is.
        // Otherwise, clear the italic decoration so the item name is not italic in the UI.
        if (parsed.style().hasDecoration(TextDecoration.ITALIC)) {
            return parsed;
        }
        return parsed.decoration(TextDecoration.ITALIC, false);
    }
}
