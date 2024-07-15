package me.iseal.powergems.misc;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.Configuration.GeneralConfigManager;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler {

    private static ExceptionHandler instance;
    private final Logger log = Bukkit.getLogger();
    private final GeneralConfigManager gcm = (GeneralConfigManager) SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);

    public static ExceptionHandler getInstance() {
        if (instance == null)
            instance = new ExceptionHandler();
        return instance;
    }

    public void dealWithException(Exception ex, Level logLevel, String errorMessage, Object... moreInfo){
        log.log(logLevel, gcm.getPluginPrefix()+"Exception triggered by "+getCallingClassName());
        log.log(logLevel, gcm.getPluginPrefix()+"The exception message is "+ex.getMessage());
        log.log(logLevel, gcm.getPluginPrefix()+"The error message is "+errorMessage);
        if (gcm.isDebugMode()) {
            log.log(Level.INFO, gcm.getPluginPrefix()+"The stacktrace and all of its details known are as follows: ");
            for (StackTraceElement stackTraceElement : ex.getStackTrace())
                log.log(Level.INFO, gcm.getPluginPrefix()+stackTraceElement.toString());
        }
        log.log(logLevel, gcm.getPluginPrefix()+"More details (make sure to tell these to the developer): ");
        for (Object obj : moreInfo) {
            log.log(logLevel, gcm.getPluginPrefix()+obj.toString());
        }
        if (logLevel == Level.SEVERE) {
            log.log(logLevel, gcm.getPluginPrefix()+"Shutting down plugin to prevent further errors");
            Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
        }
    }

    private String getCallingClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ExceptionHandler.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }
}
