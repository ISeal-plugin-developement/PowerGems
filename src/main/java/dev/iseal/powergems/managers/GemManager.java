package dev.iseal.powergems.managers;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.iseal.ExtraKryoCodecs.Enums.SerializersEnums.AnalyticsAPI.PowerGemsAnalyticsSerializers;
import dev.iseal.ExtraKryoCodecs.Holders.AnalyticsAPI.PowerGems.PGGemUsagesHourly;
import dev.iseal.powergems.PowerGems;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.sealUtils.Interfaces.Dumpable;
import dev.iseal.sealUtils.systems.analytics.AnalyticsManager;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import dev.iseal.ExtraKryoCodecs.Utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import dev.iseal.powergems.managers.Configuration.ActiveGemsConfigManager;
import dev.iseal.powergems.managers.Configuration.GemColorConfigManager;
import dev.iseal.powergems.managers.Configuration.GemLoreConfigManager;
import dev.iseal.powergems.managers.Configuration.GemMaterialConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.WrapperObjects.GemCacheItem;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class is responsible for managing the creation, identification, and
 * usage of "gems" in the game.
 * A gem is represented as an ItemStack with specific metadata.
 * Each gem has a unique power and level associated with it.
 * The class provides methods to create gems, check if an item is a gem, get a
 * random gem, and more.
 */
public class GemManager implements Dumpable {

    private static GemManager instance = null;
    public static GemManager getInstance() {
        if (instance == null) {
            instance = new GemManager();
        }
        return instance;
    }

    private GemManager() {
        dumpableInit();
    }

    // Fields for storing gem-related data and configurations
    private ItemStack randomGem = null;
    private SingletonManager sm = null;
    private ConfigManager cm = null;
    private NamespacedKeyManager nkm = null;
    private GeneralConfigManager gcm = null;
    private ActiveGemsConfigManager agcm = null;
    private GemMaterialConfigManager gmcm = null;
    private GemReflectionManager grm = null;
    private GemLoreConfigManager glcm = null;
    private GemColorConfigManager gccm = null;
    private final Random rand = new Random();
    private NamespacedKey isGemKey = null;
    private NamespacedKey gemPowerKey = null;
    private NamespacedKey gemLevelKey = null;
    private NamespacedKey gemCreationTimeKey = null;
    private final ArrayList<ChatColor> possibleColors = new ArrayList<>();
    private final Logger l = PowerGems.getPlugin().getLogger();
    private static final ArrayList<String> gemIdLookup = new ArrayList<>();
    private final HashMap<UUID, GemCacheItem> gemCache = new HashMap<>();
    private final HashMap<String, Gem> gems = new HashMap<>();
    private final HashMap<Pair<String, String>, Integer> gemUsagesByHour = new HashMap<>();

    /**
     * Initializes the gem manager with necessary keys and configurations.
     */
    public void initLater() {
        sm = SingletonManager.getInstance();
        // register default gems
        ArrayList<String> oldGems = new ArrayList<>(gemIdLookup);
        gemIdLookup.clear();
        grm = GemReflectionManager.getInstance();
        grm.registerGems();
        Collections.sort(gemIdLookup);
        gemIdLookup.addAll(oldGems);
        cm = sm.configManager;
        nkm = sm.namespacedKeyManager;
        isGemKey = nkm.getKey("is_power_gem");
        gemPowerKey = nkm.getKey("gem_power");
        gemLevelKey = nkm.getKey("gem_level");
        gemCreationTimeKey = nkm.getKey("gem_creation_time");
        removeElement(ChatColor.values(), ChatColor.MAGIC).forEach(o -> {
            if (o instanceof ChatColor) possibleColors.add((ChatColor) o);
        });
        gcm = cm.getRegisteredConfigInstance(GeneralConfigManager.class);
        agcm = cm.getRegisteredConfigInstance(ActiveGemsConfigManager.class);
        gmcm = cm.getRegisteredConfigInstance(GemMaterialConfigManager.class);
        glcm = cm.getRegisteredConfigInstance(GemLoreConfigManager.class);
        gccm = cm.getRegisteredConfigInstance(GemColorConfigManager.class);
    }

    /**
     * Removes a specific element from an array of ChatColors.
     * 
     * @param originalArray   The original array of ChatColors.
     * @param elementToRemove The element to remove from the array.
     * @return A new ArrayList of ChatColor with the specified element removed.
     */
    private ArrayList<Object> removeElement(Object[] originalArray, Object elementToRemove) {
        ArrayList<Object> list = new ArrayList<>(Arrays.asList(originalArray));
        list.removeIf(color -> color.equals(elementToRemove));
        return list;
    }

    /**
     * Looks up the ID of a gem based on its name.
     * 
     * @param gemName The name of the gem.
     * @return The ID of the gem, or -1 if the gem name is not recognized.
     */
    public static int lookUpID(String gemName) {
        if (gemIdLookup.contains(gemName)) {
            return gemIdLookup.indexOf(gemName);
        }
        return -1;
    }

    /**
     * Looks up the name of a gem based on its ID.
     * 
     * @param gemID The ID of the gem.
     * @return The name of the gem, or "Error" if the gem ID is not recognized.
     */
    public static String lookUpName(int gemID) {
        if (gemID >= 0 && gemID < gemIdLookup.size()) {
            return gemIdLookup.get(gemID);
        }
        return "Error";
    }

    /**
     * Checks if an ItemStack is a gem.
     * 
     * @param is The ItemStack to check.
     * @return True if the ItemStack is a gem, false otherwise.
     */
    public boolean isGem(ItemStack is) {
        if (is == null)
            return false;
        if (!is.hasItemMeta())
            return false;
        return is.getItemMeta().getPersistentDataContainer().has(isGemKey, PersistentDataType.BOOLEAN);
    }

    /**
     * Gets a random gem item.
     * 
     * @return An ItemStack representing a random gem.
     */
    public ItemStack getRandomGemItem() {
        if (randomGem == null) {
            randomGem = new ItemStack(gmcm.getRandomGemMaterial());
            ItemMeta gemMeta = randomGem.getItemMeta();
            gemMeta.setDisplayName(I18N.translate("RANDOM_GEM_NAME"));
            gemMeta.setCustomModelData(1);
            PersistentDataContainer pdc = gemMeta.getPersistentDataContainer();
            pdc.set(nkm.getKey("is_random_gem"), PersistentDataType.BOOLEAN, true);
            randomGem.setItemMeta(gemMeta);
        }
        return randomGem;
    }

    /**
     * Creates a gem with a specific power and level.
     * 
     * @param gemInt The power of the gem.
     * @param gemLvl The level of the gem.
     * @return An ItemStack representing the created gem.
     */
    public ItemStack createGem(int gemInt, int gemLvl) {
        return generateItemStack(gemInt, gemLvl);
    }

    /**
     * Creates a gem with a specific power and level 1.
     * 
     * @param gemInt The power of the gem.
     * @return An ItemStack representing the created gem.
     */
    public ItemStack createGem(int gemInt) {
        return generateItemStack(gemInt, 1);
    }

    /**
     * Creates a gem with a specific name and level.
     * 
     * @param gemName The name of the gem.
     * @param gemLvl  The level of the gem.
     * @return An ItemStack representing the created gem.
     */
    public ItemStack createGem(String gemName, int gemLvl) {
        return generateItemStack(lookUpID(gemName), gemLvl);
    }

    /**
     * Creates a random gem with level 1.
     * 
     * @return An ItemStack representing the created gem.
     */
    public ItemStack createGem() {
        int random = rand.nextInt(SingletonManager.TOTAL_GEM_AMOUNT);
        int repeating = 0;
        while (!agcm.isGemActive(lookUpName(random)) && repeating < gcm.getGemCreationAttempts()) {
            random = rand.nextInt(SingletonManager.TOTAL_GEM_AMOUNT);
            repeating++;
        }
        if (repeating >= gcm.getGemCreationAttempts()) {
            l.warning("Could not find a gem to create, either you got extremely unlucky or you have too many gems disabled.");
            l.warning("You can try to turn up \"gemCreationAttempts\" in the config to fix this issue.");
            return null;
        }
        return generateItemStack(random, 1);
    }

    /**
     * Creates a random gem with level 1.
     *
     * @return An ItemStack representing the created gem.
     */
    public ItemStack createGem(String[] excludedTypes) {
        if (excludedTypes == null || excludedTypes.length == 0) {
            return createGem();
        }
        if (excludedTypes.length >= SingletonManager.TOTAL_GEM_AMOUNT) {
            l.warning("You have excluded all gems, returning null.");
            return null;
        }
        int random = rand.nextInt(SingletonManager.TOTAL_GEM_AMOUNT);
        int repeating = 0;
        while (true) {
            String randomGemName = lookUpName(random);
            boolean isExcluded = Arrays.stream(excludedTypes).anyMatch(type -> type.equals(randomGemName));
            if (agcm.isGemActive(randomGemName) && !isExcluded) {
                break;
            }
            if (repeating >= gcm.getGemCreationAttempts()) {
                l.warning("Could not find a gem to create, either you got extremely unlucky or you have too many gems disabled.");
                l.warning("You can try to turn up \"gemCreationAttempts\" in the config to fix this issue.");
                return null;
            }
            random = rand.nextInt(SingletonManager.TOTAL_GEM_AMOUNT);
            repeating++;
        }
        return generateItemStack(random, 1);
    }

    /**
     * Creates a lore for the gem.
     * 
     * @param meta      The ItemMeta of the gem.
     * @param gemNumber The power of the gem.
     * @return The ItemMeta with the created lore.
     */
    public ItemMeta createLore(ItemMeta meta, int gemNumber) {
        if (!gcm.doGemDescriptions()) {
            return meta;
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(gemLevelKey, PersistentDataType.INTEGER)) {
            pdc.set(gemLevelKey, PersistentDataType.INTEGER, 1);
        }
        int gemLevel = pdc.get(gemLevelKey, PersistentDataType.INTEGER);
        ArrayList<String> lore = new ArrayList<>();
        meta.setLore(lore);
        lore.addAll(glcm.getLore(gemNumber));
        // replace %level% with the actual level
        lore.forEach(line -> {
            if (line.contains("%level%"))
                lore.set(lore.indexOf(line), line.replace("%level%", gemLevel + ""));
        });
        meta.setLore(lore);
        return meta;
    }

    /**
     * Creates a lore for the gem.
     *
     * @param meta The ItemMeta of the gem.
     * @return The ItemMeta with the created lore.
     */
    public ItemMeta createLore(ItemMeta meta) {
        if (meta.getPersistentDataContainer().has(gemPowerKey, PersistentDataType.STRING)) {
            return createLore(meta,
                    lookUpID(meta.getPersistentDataContainer().get(gemPowerKey, PersistentDataType.STRING)));
        } else {
            return createLore(meta, 1);
        }
    }

    /**
     * Returns a color code string based on the gem number.
     * If the configuration allows randomized colors, a random color is chosen from
     * the possible colors.
     * Otherwise, a specific color is assigned based on the gem number.
     * 
     * @param gemName the name of the gem, as specified in gemLookupMap.
     * @return A string representing the color code.
     */
    private String getColor(String gemName) {
        if (gcm.isRandomizedColors()) {
            return possibleColors.get(rand.nextInt(possibleColors.size())).toString();
        }
        return gccm.getGemColor(gemName).toString();
    }

    /**
     * Generates an ItemStack for a gem.
     * 
     * @param gemNumber The power of the gem.
     * @param gemLevel  The level of the gem.
     * @return An ItemStack representing the created gem.
     */
    private ItemStack generateItemStack(int gemNumber, int gemLevel) {
        ItemStack gemItem = new ItemStack(gmcm.getGemMaterial(lookUpName(gemNumber)));
        ItemMeta reGemMeta = gemItem.getItemMeta();
        reGemMeta.setDisplayName(getColor(lookUpName(gemNumber)) + lookUpName(gemNumber) + " Gem");
        PersistentDataContainer reDataContainer = reGemMeta.getPersistentDataContainer();
        reDataContainer.set(isGemKey, PersistentDataType.BOOLEAN, true);
        reDataContainer.set(gemPowerKey, PersistentDataType.STRING, lookUpName(gemNumber));
        reDataContainer.set(gemLevelKey, PersistentDataType.INTEGER, gemLevel);
        reDataContainer.set(gemCreationTimeKey, PersistentDataType.LONG, System.currentTimeMillis());
        reGemMeta = createLore(reGemMeta, gemNumber);
        // index 0 empty, index 1 random gem, index 2-x gems
        // might want to make an index for every level of gem in the future, could be fun
        reGemMeta.setCustomModelData(gemNumber+2);
        gemItem.setItemMeta(reGemMeta);
        //int customModelData = reGemMeta.hasCustomModelData() ? reGemMeta.getCustomModelData() : -1;
        //l.info(gcm.getPluginPrefix() + "Created a " 
        //+ lookUpName(gemNumber) 
        //+ " gem with custom model data " + customModelData);
        return gemItem;
    }

    /**
     * Returns a HashMap of all gems.
     * Each gem is represented as an ItemStack, and the key is the gem number.
     * 
     * @return A HashMap where the keys are gem numbers and the values are
     *         ItemStacks representing the gems.
     */
    public HashMap<Integer, ItemStack> getAllGems() {
        HashMap<Integer, ItemStack> allGems = new HashMap<>(10);
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            allGems.put(i, generateItemStack(i, 1));
        }
        return allGems;
    }

    /**
     * Returns a list of all gems in a player's inventory.
     * Each gem is represented as an ItemStack.
     * 
     * @param plr The player whose inventory to check.
     * @return An ArrayList of ItemStacks representing the gems in the player's
     *         inventory.
     */
    public ArrayList<ItemStack> getPlayerGems(Player plr) {
        // isValid() checks if the cache is expired or if something became null - bukkit be weird fr fr
        if (gemCache.containsKey(plr.getUniqueId()) && gemCache.get(plr.getUniqueId()).isValid()) {
            return gemCache.get(plr.getUniqueId()).getOwnedGems();
        }
        ArrayList<ItemStack> foundGems = new ArrayList<>(1);
        Arrays.stream(plr.getInventory().getContents().clone()).filter(this::isGem).forEach(foundGems::add);
        if (isGem(plr.getInventory().getItemInOffHand()))
            foundGems.add(plr.getInventory().getItemInOffHand());
        gemCache.put(plr.getUniqueId(), new GemCacheItem(foundGems));
        return foundGems;
    }

    public Gem getGemInstance(ItemStack item, Player plr) {
        if (!isGem(item)) {
            return null;
        }
        return grm.getGemInstance(item, plr);
    }

    /**
     * Returns the level of a gem.
     * If the item is not a gem, returns 0.
     * 
     * @param item The ItemStack to check.
     * @return The level of the gem, or 0 if the item is not a gem.
     */
    public int getLevel(ItemStack item) {
        if (!isGem(item))
            return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(gemLevelKey, PersistentDataType.INTEGER)) {
            pdc.set(gemLevelKey, PersistentDataType.INTEGER, 1);
        }
        return pdc.get(gemLevelKey, PersistentDataType.INTEGER);
    }

    /**
     * Returns the name of a gem's power.
     * If the item is invalid, returns an empty string
     * 
     * @param item The ItemStack to check.
     * @return The name of the gem's power, or an empty string if the item is
     *         invalid
     */
    public String getName(ItemStack item) {
        if (!isGem(item))
            return "";
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(gemPowerKey, PersistentDataType.STRING)) {
            return "";
        }
        return pdc.get(gemPowerKey, PersistentDataType.STRING);
    }

    /**
     * Returns the class of a gem.
     * If the item is not a gem, returns null.
     * 
     * @param item The ItemStack to check.
     * @param plr The player performing the action. Used to auto-fix the gem in case of errors.
     * @return The Class object representing the gem's class, or null if the item is
     *         not a gem.
     */
    public Class<?> getGemClass(ItemStack item, Player plr) {
        return grm.getGemClass(item, plr);
    }

    /**
     * Runs a method call on a gem.
     * If the item is not a gem, throws an IllegalArgumentException.
     * 
     * @param item   The ItemStack representing the gem.
     * @param action The Action to perform.
     * @param plr    The player performing the action.
     *
     * @throws IllegalArgumentException If the item is not a gem.
     */
    public void runCall(ItemStack item, Action action, Player plr) {
        if (!grm.runCall(item, action, plr))
            ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("NOT_A_GEM"), Level.WARNING,
                    "The item passed in is not a gem", item);
    }

    public boolean areGemsEqual(ItemStack gem1, ItemStack gem2) {
        if (!isGem(gem1) || !isGem(gem2)) {
            return false;
        }
        boolean powerEqual = getName(gem1).equals(getName(gem2));
        boolean levelEqual = getLevel(gem1) == getLevel(gem2);
        return powerEqual && levelEqual;
    }

    /*
    * Returns the gem creation time
    * 
    * @return The gem creation time, or -1 if it is not a gem
     */
    public long getGemCreationTime(ItemStack item) {
        if (!isGem(item)) {
            return -1;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(gemCreationTimeKey, PersistentDataType.LONG)) {
            //old gem (migrate it)
            pdc.set(gemCreationTimeKey, PersistentDataType.LONG, System.currentTimeMillis());
        }
        return pdc.get(gemCreationTimeKey, PersistentDataType.LONG);
    }

    public Particle runParticleCall(ItemStack item, Player plr) {
        return grm.runParticleCall(item, plr);
    }

    /**
     * Adds a gem to the manager.
     *
     * @param gem The Gem object to add.
     */
    public void addGem(Gem gem) {
        String name = gem.getName();
        if (gemIdLookup.contains(name)) {
            l.warning("Gem with name " + name + " already exists, skipping.");
            return;
        }
        gemIdLookup.add(gem.getName());
        gems.put(name, gem);
        l.info("Registered gem: " + name);
    }

    /**
     * Returns a HashMap of all gems.
     * Each gem is represented as an ItemStack, and the key is the gem number.
     * 
     * @return A HashMap where the keys are gem numbers and the values are
     *         ItemStacks representing the gems.
     */
    public HashMap<String, Gem> getGems() {
        return gems; // Return the internal HashMap containing all registered gems
    }

    public void attemptFixGem(ItemStack item) {
        String name = getName(item);
        if (Objects.equals(name, "")) {
            // gem is just too fucking broken
            // or is not an actual gem
            return;
        }

        boolean broken = false;
        int id = lookUpID(name);
        ItemMeta meta = item.getItemMeta();

        // fix for broken model data
        if (meta.getCustomModelData() != id+2) {
            meta.setCustomModelData(id + 2);
            broken = true;
        }

        // fix for gems having "Gem" in the name
        if (name.endsWith("Gem")) {
            meta.getPersistentDataContainer().set(gemPowerKey, PersistentDataType.STRING, name.substring(0, name.length() - 3));
            broken = true;
        }

        if (broken) {
            // finally, set the new meta.
            item.setItemMeta(meta);
            l.warning("An error in a gem has been found and fixed!");
        }
    }

    /**
     * Adds a gem usage to the temp storage.
     * This method is used to track how many times a gem with a specific ability is utilized
     * @param gemName the name of the gem
     * @param ability the ability of the gem that was used
     */
    public void addGemUsage(String gemName, String ability) {
        if (!gcm.isAllowMetrics()) return;
        if (gemName == null || gemName.isEmpty()) {
            l.warning("Tried to add gem usage with null or empty name, skipping.");
            return;
        }
        if (ability == null || ability.isEmpty()) {
            l.warning("Tried to add gem usage with null or empty ability, skipping.");
            return;
        }
        Pair<String, String> key = new Pair<>(gemName, ability);
        gemUsagesByHour.putIfAbsent(key, 0);
        gemUsagesByHour.put(key, gemUsagesByHour.get(key) + 1);
        if (gcm.isDebugMode())
            l.info("Added gem usage for " + gemName + " with ability " + ability + ". Total usages: " + gemUsagesByHour.get(key));
    }

    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> dump = new HashMap<>();
        dump.put("randomGem", randomGem);
        dump.put("sm", sm);
        dump.put("cm", cm);
        dump.put("nkm", nkm);
        dump.put("gcm", gcm);
        dump.put("agcm", agcm);
        dump.put("gmcm", gmcm);
        dump.put("grm", grm);
        dump.put("glcm", glcm);
        dump.put("rand", rand);
        dump.put("isGemKey", isGemKey);
        dump.put("gemPowerKey", gemPowerKey);
        dump.put("gemLevelKey", gemLevelKey);
        dump.put("gemCreationTimeKey", gemCreationTimeKey);
        dump.put("possibleColors", possibleColors);
        dump.put("gemIdLookup", gemIdLookup);
        return dump;
    }
}
