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
    
    public static ExceptionHandler getInstance() {
        if (instance == null)
            instance = new ExceptionHandler();
        return instance;
    }

    public void dealWithException(Exception ex, Level logLevel, String errorMessage, Object... moreInfo){
        log.log(logLevel, "[Powergems] "+"Exception triggered by "+getCallingClassName());
        log.log(logLevel, "[Powergems] "+"The exception message is "+ex.getMessage());
        log.log(logLevel, "[Powergems] "+"The error message is "+errorMessage);
        log.log(Level.INFO, "[Powergems] "+"The stacktrace and all of its details known are as follows: ");
        for (StackTraceElement stackTraceElement : ex.getStackTrace())
            log.log(Level.INFO, "[Powergems] "+stackTraceElement.toString());

        log.log(logLevel, "[Powergems] "+"More details (make sure to tell these to the developer): ");
        for (Object obj : moreInfo) {
            log.log(logLevel, "[Powergems] "+obj.toString());
        }
        if (logLevel == Level.SEVERE) {
            log.log(logLevel, "[Powergems] "+"Shutting down plugin to prevent further errors");
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
