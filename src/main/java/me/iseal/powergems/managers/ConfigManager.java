package me.iseal.powergems.managers;

import me.iseal.powergems.Main;
import me.iseal.powergems.misc.AbstractConfigManager;
import me.iseal.powergems.misc.ExceptionHandler;
import me.iseal.powergems.misc.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class ConfigManager {

    private final ArrayList<Class< ? extends AbstractConfigManager>> registeredConfigurations = new ArrayList<>(5);
    private final HashMap<Class<? extends AbstractConfigManager>, Object> registeredConfigInstances = new HashMap<>(5);

    public void setUpConfig() {
        Utils.findAllClassesInPackage("me.iseal.powergems.managers.Configuration").forEach(this::addConfigClass);
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
    * @return The instance, registering it if needed or null if it is not a possible config object
     */
    public <T extends AbstractConfigManager> T getRegisteredConfigInstance(Class<T> clazz) {
        if (!isPossibleConfigClass(clazz)) {
            return null;
        }
        if (!registeredConfigurations.contains(clazz)){
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("EARLY_ASK_FOR_CONFIG_INSTANCE"), Level.WARNING, "EARLY_ASK_FOR_CONFIG_INSTANCE", clazz.getName());
        }
        if (!registeredConfigInstances.containsKey(clazz)){
            registerConfigInstance(clazz);
        }
        return clazz.cast(registeredConfigInstances.get(clazz));
    }

    private void registerConfigInstance(Class<? extends AbstractConfigManager> clazz) {
        if (!isPossibleConfigClass(clazz)) {
            return;
        }
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

    private boolean isPossibleConfigClass(Class<?> clazz) {
        return AbstractConfigManager.class.isAssignableFrom(clazz);
    }

    private void addConfigClass(Class<?> clazz) {
        if (isPossibleConfigClass(clazz)) {
            registeredConfigurations.add((Class<? extends AbstractConfigManager>) clazz);
        }
    }

}
