package dev.iseal.powergems.misc;

import dev.iseal.powergems.PowerGems;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler {

    private static ExceptionHandler instance;
    private final Logger log = Bukkit.getLogger();
    public boolean hasErrors = false;
    public ArrayList<String> errorMessages = new ArrayList<>();

    public static ExceptionHandler getInstance() {
        if (instance == null)
            instance = new ExceptionHandler();
        return instance;
    }

    public void dealWithException(Exception ex, Level logLevel, String errorMessage, Object... moreInfo){
        log.log(logLevel, "[PowerGems] "+"Exception triggered by "+getCallingClassName());
        log.log(logLevel, "[PowerGems] "+"The exception message is "+ex.getMessage());
        log.log(logLevel, "[PowerGems] "+"The error message is "+errorMessage);
        log.log(Level.INFO, "[PowerGems] "+"The stacktrace and all of its details known are as follows: ");
        for (StackTraceElement stackTraceElement : ex.getStackTrace())
            log.log(logLevel, "[PowerGems] "+stackTraceElement.toString());

        log.log(logLevel, "[PowerGems] "+"More details (make sure to tell these to the developer): ");
        int i = 1;
        for (Object obj : moreInfo) {
            log.log(logLevel, "[PowerGems] More info "+i+": "+obj.toString());
            i++;
        }
        hasErrors = true;
        this.errorMessages.add(errorMessage);
        if (logLevel == Level.SEVERE) {
            log.log(logLevel, "[PowerGems] "+"Shutting down plugin to prevent further errors");
            Bukkit.getPluginManager().disablePlugin(PowerGems.getPlugin());
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
