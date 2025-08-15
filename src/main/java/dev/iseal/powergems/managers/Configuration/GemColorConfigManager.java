package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class GemColorConfigManager extends AbstractConfigManager {

    public GemColorConfigManager() {
        super("GemColor");
    }

    @Override
    public void setUpConfig() {

    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            file.setDefault(GemManager.lookUpName(i) + "GemColor", NamedTextColor.GREEN.asHexString());
        }
    }

    public TextColor getGemColor(String gemName) {
        String colorValue = file.getOrSetDefault(gemName+"GemColor", NamedTextColor.GREEN.asHexString());
        try {
            return TextColor.fromHexString(colorValue);
        } catch (IllegalArgumentException e) {
            try {
                return NamedTextColor.NAMES.value(colorValue.toLowerCase());
            } catch (Exception ex) {
                return NamedTextColor.GREEN;
            }
        }
    }
}
