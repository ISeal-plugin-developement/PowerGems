package dev.iseal.powergems.managers;

import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Interfaces.Dumpable;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import dev.iseal.sealLib.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GemReflectionManager implements Dumpable {

    private static GemReflectionManager instance;
    public static GemReflectionManager getInstance() {
        if (instance == null)
            instance = new GemReflectionManager();
        return instance;
    }

    private final HashMap<Class< ? extends Gem>, Gem> registeredGems = new HashMap<>(10);
    private final Logger l = Bukkit.getLogger();
    private final SingletonManager sm = SingletonManager.getInstance();
    private final GemManager gm = sm.gemManager;
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    public void registerGems() {
        GlobalUtils .findAllClassesInPackage("dev.iseal.powergems.gems", Gem.class)
                .forEach(this::addGemClass);
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
            try {
                Gem instance = (Gem) clazz.getDeclaredConstructor().newInstance();
                registeredGems.put((Class<? extends Gem>) clazz, instance);
                SingletonManager.TOTAL_GEM_AMOUNT++;
                gm.addGem(instance);
            } catch (Exception e) {
                ExceptionHandler.getInstance().dealWithException(e, Level.SEVERE, "ADD_GEM_CLASS", clazz.getName());
                return false;
            }
            return true;
        } else
            return false;
    }
    
    public Class<? extends Gem> getGemClass(ItemStack gem ,Player plr) {
        if (!gm.isGem(gem))
            return null;
        String gem_power = Objects.requireNonNull(gem.getItemMeta()).getPersistentDataContainer().get(nkm.getKey("gem_power"), PersistentDataType.STRING)+"Gem";
        if (gem_power.equals("ErrorGem")) {
            l.warning("A bugged gem has been found! Attempting to fix this.");
            plr.getInventory().remove(gem);
            gem = gm.createGem();
            plr.getInventory().addItem(gem);
        }
        Class<? extends Gem> gemclass = registeredGems.keySet().stream()
                .filter(clazz -> clazz.getSimpleName().equals(gem_power))
                .findFirst()
                .orElse(null);
        if (gemclass == null) {
            l.warning("A bugged gem has been found! Did you add / remove any addons? Attempting to fix this.");
            plr.getInventory().remove(gem);
            gem = gm.createGem();
            plr.getInventory().addItem(gem);
        }
        return gemclass;
    }

    public ArrayList<Class<? extends Gem>> getAllGemsClasses() {
        return new ArrayList<>(registeredGems.keySet());
    }

    public boolean runCall(ItemStack item, Action action, Player plr) {
        if (!gm.isGem(item)) {
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("The item passed in is not a gem"), Level.WARNING, "NOT_A_GEM", item);
            return false;
        }
        Class<? extends Gem> gemClass = getGemClass(item, plr);
        if (gemClass == null) {
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("The gem class was not found"), Level.WARNING, "GEM_CLASS_NOT_FOUND", item);
            return false;
        }
        if (!registeredGems.containsKey(gemClass)) {
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("The gem has not been registered"), Level.WARNING, "GEM_NOT_REGISTERED", item);
            return false;
        }
        Gem gemInstance = registeredGems.get(gemClass);
        gemInstance.call(action, plr, item);
        return true;
    }

    public Particle runParticleCall(ItemStack item, Player plr) {
        if (!gm.isGem(item)) {
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("The item passed in is not a gem"), Level.WARNING, "NOT_A_GEM", item);
            return null;
        }
        Class<? extends Gem> gemClass = getGemClass(item, plr);
        if (gemClass == null) {
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("The gem class was not found"), Level.WARNING, "GEM_CLASS_NOT_FOUND", item);
            return null;
        }
        if (!registeredGems.containsKey(gemClass)) {
            ExceptionHandler.getInstance().dealWithException(new RuntimeException("The gem has not been registered"), Level.WARNING, "GEM_NOT_REGISTERED", item);
            return null;
        }
        Gem gemInstance = registeredGems.get(gemClass);
        return gemInstance.particle();
    }

    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> dump = new HashMap<>();
        dump.put("registeredGems", registeredGems);
        dump.put("sm", sm);
        dump.put("gm", gm);
        dump.put("nkm", nkm);
        return dump;
    }
}
