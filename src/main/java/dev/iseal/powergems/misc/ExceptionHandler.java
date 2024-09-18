package dev.iseal.powergems.misc;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.Interfaces.Dumpable;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

        dumpAllClasses(logLevel);

        if (logLevel == Level.SEVERE) {
            hasErrors = true;
            this.errorMessages.add(errorMessage);
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

    public void dumpAllClasses(Level logLevel) {
        Set<Class<?>> dumpableClasses = Utils.findAllClassesInPackage("dev.iseal.powergems", Dumpable.class);

        dumpableClasses.forEach(clazz -> {
            if (clazz.equals(Dumpable.class)) return;
            // check if class is singleton
            if (clazz.getDeclaredMethods().length == 0) return;
            AtomicBoolean done = new AtomicBoolean(false);

            Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.getName().equals("getInstance")).findFirst().ifPresent(getInstance -> {
                try {
                    Object instance = getInstance.invoke(null);
                    Method dump = clazz.getDeclaredMethod("dump");
                    log.log(logLevel, "[BossAPI] Class "+ clazz.getSimpleName() + " dump: " + dump.invoke(instance).toString());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.log(Level.SEVERE, "[BossAPI] "+"Error while trying to dump class "+clazz.getSimpleName());
                }
                done.set(true);
            });
            if (done.get()) return;
            // Check if SingletonManager contains reference to class (I should really change this to use .getInstance() method)
            Arrays.stream(SingletonManager.class.getDeclaredFields()).filter(field -> field.getType().equals(clazz)).findFirst().ifPresent(field -> {
                try {
                    Object instance = field.get(SingletonManager.getInstance());
                    Method dump = clazz.getDeclaredMethod("dump");
                    log.log(logLevel, "[BossAPI] Class "+ clazz.getSimpleName() + " dump: " + dump.invoke(instance).toString());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    log.log(Level.SEVERE, "[BossAPI] "+"Error while trying to dump class "+clazz.getSimpleName());
                }
                done.set(true);
            });
            if (done.get()) return;
            // Last resort, create new instance and dump. 99% of the time this will fail, someone screwed up Dumpable implementation real bad
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method dump = clazz.getDeclaredMethod("dump");
                log.log(logLevel, "[BossAPI] Class "+ clazz.getSimpleName() + " dump: " + dump.invoke(instance).toString());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | IllegalArgumentException e1) {
                log.log(Level.SEVERE, "[BossAPI] "+"Error while trying to dump class "+clazz.getSimpleName());
                done.set(true);
            }
            if (done.get()) return;
        });
    }
}
