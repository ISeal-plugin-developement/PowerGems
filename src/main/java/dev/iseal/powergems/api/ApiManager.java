package dev.iseal.powergems.api;

import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ApiManager {

    private static ApiManager instance = null;
    public static ApiManager getInstance() {
        if (instance == null)
            instance = new ApiManager();
        return instance;
    }

    private final GemReflectionManager grm = GemReflectionManager.getInstance();
    public boolean acceptsGems = true;
    private final HashMap<Class<? extends Gem>, JavaPlugin> gemPlugins = new HashMap<>();

    public boolean registerGemClass(Class<? extends Gem> gemClass, JavaPlugin addonPlugin) {
        if (!acceptsGems) {
            Bukkit.getLogger().info(I18N.translate("GEMS_NOT_ACCEPTED"));
            return false;
        }
        boolean success = grm.addGemClass(gemClass);
        if (!success)
            Bukkit.getLogger().info(I18N.translate("FAIL_REGISTER_GEM_CLASS").replace("{class}", gemClass.getName()));
        else
            gemPlugins.put(gemClass, addonPlugin);
        return success;
    }

    public List<JavaPlugin> getGemPlugins() {
        return new ArrayList<>(gemPlugins.values());
    }

}
