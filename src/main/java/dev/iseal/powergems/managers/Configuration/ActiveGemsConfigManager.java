package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

public class ActiveGemsConfigManager extends AbstractConfigManager {

    public ActiveGemsConfigManager() {
        super("activeGems");
    }

    @Override
    public void setUpConfig() {
        file.setHeader(
                "This config file manages whether a gem is active or not",
                "True means the gem is active(disabled)",
                "False means the gem is inactive(enabled)");    

    }

    @Override
    public void lateInit() {

    }

    public boolean isGemActive(String name) {
        return file.getOrSetDefault(name + "GemActive", true);
    }

}
