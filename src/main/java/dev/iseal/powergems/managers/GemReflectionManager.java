package dev.iseal.powergems.managers;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.ExceptionHandler;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GemReflectionManager {

    private static GemReflectionManager instance;
    public static GemReflectionManager getInstance() {
        if (instance == null)
            instance = new GemReflectionManager();
        return instance;
    }

    private final ArrayList<Class< ? extends Gem>> registeredGems = new ArrayList<>(10);
    private final HashMap<Class<? extends Gem>, Object> registeredGemInstances = new HashMap<>(10);
    private final Logger l = Bukkit.getLogger();
    private final SingletonManager sm = SingletonManager.getInstance();
    private final GemManager gm = sm.gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    public void registerGems() {
        Utils.findAllClassesInPackage("dev.iseal.powergems.gems", Gem.class)
                .forEach(gem -> {
                    addGemClass(gem);
                    registerGemInstance((Class<? extends Gem>) gem);
                });
    }

    public boolean registerGemInstance(Class<? extends Gem> clazz) {
        if (!isPossibleGemClass(clazz)) {
            l.log(Level.WARNING, "Class {0} is not a possible gem class.", clazz.getName());
            return false;
        }
        try {
            if (!registeredGemInstances.containsKey(clazz)) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                registeredGemInstances.put(clazz, instance);
                return true;
            }
        } catch (NoSuchMethodException ex) {
            l.log(Level.SEVERE, "No default constructor found for class {0}.", clazz.getName());
            ExceptionHandler.getInstance().dealWithException(ex, Level.SEVERE, "REGISTER_GEM_INSTANCE");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            l.log(Level.SEVERE, "Error instantiating class {0}.", clazz.getName());
            if (ex instanceof InvocationTargetException) {
                l.info(((InvocationTargetException) ex).getTargetException().getMessage());
                Arrays.stream(((InvocationTargetException) ex).getTargetException().getStackTrace())
                        .forEach(stackTraceElement -> l.info(stackTraceElement.toString()));
            } else
                ExceptionHandler.getInstance().dealWithException(ex, Level.SEVERE, "REGISTER_GEM_INSTANCE");
        }
        return false;
    }

    private boolean isPossibleGemClass(Class<?> clazz) {
        boolean isAbstractGemClass = Gem.class.isAssignableFrom(clazz);
        if (!isAbstractGemClass) {
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("NOT_A_GEM_CLASS"), Level.WARNING, "The current class was passed in and is not a gem class", clazz.getName());
        }
        return isAbstractGemClass;
    }

    public boolean addGemClass(Class<?> clazz) {
        if (isPossibleGemClass(clazz)) {
            registeredGems.add((Class<? extends Gem>) clazz);
            return true;
        } else
            return false;
    }
    
    public Class<? extends Gem> getGemClass(ItemStack gem) {
        if (!gm.isGem(gem))
            return null;
        String gem_power = gem.getItemMeta().getPersistentDataContainer().get(nkm.getKey("gem_power"), PersistentDataType.STRING);
        return registeredGems.stream()
                .filter(clazz -> clazz.getSimpleName().equals(gem_power))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Class<? extends Gem>> getAllGemsClasses() {
        return new ArrayList<>(registeredGems);
    }

    public boolean runCall(ItemStack item, Action action, Player plr) {
        if (!gm.isGem(item))
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("NOT_A_GEM"), Level.WARNING, "The item passed in is not a gem", item);
        Class<? extends Gem> gemClass = getGemClass(item);
        if (gemClass == null)
            return false;
        Object gemInstance = registeredGemInstances.get(gemClass);
        try {
            Method call = gemClass.getMethod("call", Action.class, Player.class, ItemStack.class);
            call.invoke(gemInstance, action, plr, item);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ExceptionHandler.getInstance().dealWithException(ex, Level.SEVERE, "RUN_CALL", gemClass);
        }
        return false;
    }

    public Particle runParticleCall(ItemStack item, Player plr) {
        if (!gm.isGem(item))
            return null;
        Class<? extends Gem> gemClass = getGemClass(item);
        if (gemClass == null)
            return null;
        Object gemInstance = registeredGemInstances.get(gemClass);
        try {
            Method call = gemClass.getMethod("particle", Player.class);
            return (Particle) call.invoke(gemInstance, plr);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ExceptionHandler.getInstance().dealWithException(ex, Level.SEVERE, "RUN_PARTICLE_CALL", gemClass);
        }
        return null;
    }
}
