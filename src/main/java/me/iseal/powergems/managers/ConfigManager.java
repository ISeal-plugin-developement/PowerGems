package me.iseal.powergems.managers;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.Configuration.*;
import me.iseal.powergems.misc.ExceptionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class ConfigManager {

    private final ArrayList<Class< ? extends AbstractConfigManager>> registeredConfigurations = new ArrayList<>(5);
    private final HashMap<Class<? extends AbstractConfigManager>, Object> registeredConfigInstances = new HashMap<>(5);

    public void setUpConfig() {
        try {
            for (Class<? extends AbstractConfigManager> currentClass : registeredConfigurations) {
                Object instance = getRegisteredConfigInstance(currentClass);
                Method init = currentClass.getMethod("setUpConfig");
                init.invoke(instance);
            }
        } catch(Exception ex){
            ExceptionHandler.getInstance().dealWithException(ex, Level.SEVERE, "SET_UP_CONFIG");
        }
    }

    /*
    * Gets the instance of a registered config object
    *
    * @return The instance, registering it if needed
     */
    public Object getRegisteredConfigInstance(Class<? extends AbstractConfigManager> clazz) {
        if (!registeredConfigInstances.containsKey(clazz)){
            registerConfigInstance(clazz);
        }
        return registeredConfigInstances.get(clazz);
    }

    private void registerConfigInstance(Class<? extends AbstractConfigManager> clazz) {
        try {
            if (!registeredConfigInstances.containsKey(clazz)) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                registeredConfigInstances.put(clazz, instance);
            }
        } catch (Exception ex) {
            ExceptionHandler.getInstance().dealWithException(ex, Level.SEVERE, "RESISTER_CONFIG_INSTANCE");
        }
    }

    public static String getConfigFolderPath(){
        return Main.getPlugin().getDataFolder() + "\\config\\";
    }

    public void resetConfig() {
        for (Class<? extends AbstractConfigManager> currentClass : registeredConfigurations) {
            try {
                Object instance = getRegisteredConfigInstance(currentClass);
                Method init = currentClass.getMethod("resetConfig");
                init.invoke(instance);
            } catch (Exception ex) {
                ExceptionHandler.getInstance().dealWithException(ex, Level.SEVERE, "RESET_CONFIG", currentClass);
            }
        }
    }

    public void addConfigClass(Class< ? extends AbstractConfigManager > clazz) {
        if (!registeredConfigurations.contains(clazz))
            registeredConfigurations.add(clazz);
    }

}
