package me.iseal.powergems.managers;

import me.iseal.powergems.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

/**
 * This class is responsible for managing the creation, identification, and
 * usage of "gems" in the game.
 * A gem is represented as an ItemStack with specific metadata.
 * Each gem has a unique power and level associated with it.
 * The class provides methods to create gems, check if an item is a gem, get a
 * random gem, and more.
 */
public class GemManager {

    // Fields for storing gem-related data and configurations
    private ItemStack randomGem = null;
    private ConfigManager cm = null;
    private Random rand = new Random();
    private NamespacedKey isGemKey = null;
    private NamespacedKey gemPowerKey = null;
    private NamespacedKey gemLevelKey = null;
    private ArrayList<ChatColor> possibleColors = new ArrayList<>();
    private final Logger l = Bukkit.getLogger();

    /**
     * Initializes the gem manager with necessary keys and configurations.
     */
    public void initLater() {
        isGemKey = Main.getIsGemKey();
        gemPowerKey = Main.getGemPowerKey();
        gemLevelKey = Main.getGemLevelKey();
        possibleColors = removeElement(ChatColor.values(), ChatColor.MAGIC);
        cm = Main.getSingletonManager().configManager;
    }

    /**
     * Removes a specific element from an array of ChatColor.
     * 
     * @param originalArray   The original array of ChatColor.
     * @param elementToRemove The element to remove from the array.
     * @return A new ArrayList of ChatColor with the specified element removed.
     */
    private ArrayList<ChatColor> removeElement(ChatColor[] originalArray, ChatColor elementToRemove) {
        ArrayList<ChatColor> list = new ArrayList<>(Arrays.asList(originalArray));
        list.removeIf(color -> color.equals(elementToRemove));
        return list;
    }

    /**
     * Looks up the ID of a gem based on its name.
     * 
     * @param gemName The name of the gem.
     * @return The ID of the gem, or -1 if the gem name is not recognized.
     */
    public int lookUpID(String gemName) {
        return switch (gemName) {
            case "Strength" -> 1;
            case "Healing" -> 2;
            case "Air" -> 3;
            case "Fire" -> 4;
            case "Iron" -> 5;
            case "Lightning" -> 6;
            case "Sand" -> 7;
            case "Ice" -> 8;
            case "Lava" -> 9;
            case "Water" -> 10;
            default -> -1;
        };
    }

    /**
     * Looks up the name of a gem based on its ID.
     * 
     * @param gemID The ID of the gem.
     * @return The name of the gem, or "Error" if the gem ID is not recognized.
     */
    public String lookUpName(int gemID) {
        return switch (gemID) {
            case 1 -> "Strength";
            case 2 -> "Healing";
            case 3 -> "Air";
            case 4 -> "Fire";
            case 5 -> "Iron";
            case 6 -> "Lightning";
            case 7 -> "Sand";
            case 8 -> "Ice";
            case 9 -> "Lava";
            case 10 -> "Water";
            default -> "Error";
        };
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
        if (is.getItemMeta().getPersistentDataContainer().has(isGemKey, PersistentDataType.BOOLEAN))
            return true;
        return false;
    }

    /**
     * Gets a random gem item.
     * 
     * @return An ItemStack representing a random gem.
     */
    public ItemStack getRandomGemItem() {
        if (randomGem == null) {
            randomGem = new ItemStack(Material.EMERALD);
            ItemMeta gemMeta = randomGem.getItemMeta();
            gemMeta.setDisplayName(ChatColor.GREEN + "Random Gem");
            PersistentDataContainer pdc = gemMeta.getPersistentDataContainer();
            pdc.set(Main.getIsRandomGemKey(), PersistentDataType.BOOLEAN, true);
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
     * Creates a gem with a specific name and level 1.
     * 
     * @param gemName The name of the gem.
     * @return An ItemStack representing the created gem.
     * @deprecated Use createGem(gemInt) instead.
     */
    @Deprecated(since = "3.3.2.2")
    public ItemStack createGem(String gemName) {
        return generateItemStack(lookUpID(gemName), 1);
    }

    /**
     * Creates a random gem with level 1.
     * 
     * @return An ItemStack representing the created gem.
     */
    public ItemStack createGem() {
        int random = rand.nextInt(10) + 1;
        int repeating = 0;
        while (!cm.isGemActive(lookUpName(random)) && repeating < cm.getGemCreationAttempts()) {
            random = rand.nextInt(10) + 1;
            repeating++;
        }
        if (repeating >= cm.getGemCreationAttempts()) {
            l.warning(
                    "Could not find a gem to create, either you got extremely unlucky or you have too many gems disabled.");
            l.warning("You can try to turn up \"gemCreationAttempts\" in the config to fix this issue.");
            return null;
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
        ArrayList<String> lore = new ArrayList<>();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(gemLevelKey, PersistentDataType.INTEGER)) {
            pdc.set(gemLevelKey, PersistentDataType.INTEGER, 1);
        }
        lore.add(ChatColor.DARK_BLUE + "Level: " + ChatColor.DARK_GREEN
                + pdc.get(gemLevelKey, PersistentDataType.INTEGER));

        if (cm.doGemDescriptions()) {
            switch (gemNumber) {
                case 1:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE + "Right click: Saturation, Strength and Resistance (all lvl 2)");
                    lore.add(ChatColor.WHITE + "Shift click: An arena that keeps anyone from entering, useful to heal");
                    lore.add(ChatColor.WHITE
                            + "Left click: A shockwave that sends everyone near flying and damages them");
                    break;
                case 2:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE + "Right click: Parry");
                    lore.add(ChatColor.WHITE + "Shift click: Instant heal");
                    lore.add(ChatColor.WHITE + "Left click: 1 minute of regeneration 2");
                    break;
                case 3:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE
                            + "Right click: Creates a tether of wind between the player and a target player, pulling the target closer.");
                    lore.add(ChatColor.WHITE
                            + "Shift click: Creates a cloud of smoke, granting temporary invisibility and propelling the player forward.");
                    lore.add(ChatColor.WHITE
                            + "Left click: Unleashes a burst of wind, launching nearby entities into the air and dealing damage.");
                    break;
                case 4:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE
                            + "Right click: Creates a fiery aura around the player, granting fire resistance and igniting nearby air blocks.");
                    lore.add(ChatColor.WHITE
                            + "Shift click: Triggers a powerful explosion at the player's location, damaging nearby entities and applying fire damage.");
                    lore.add(ChatColor.WHITE
                            + "Left click: Launches a fireball in the direction the player is facing, causing an explosion upon impact.");
                    break;
                case 5:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE
                            + "Right click: Temporarily grants the player increased absorption and knockback resistance.");
                    lore.add(ChatColor.WHITE
                            + "Shift click: Temporarily increases the player's armor and armor toughness.");
                    lore.add(ChatColor.WHITE + "Left click: Fires a barrage of spectral arrows in a circle shape.");
                    break;
                case 6:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE
                            + "Right click: Strikes lightning at the target location and nearby entities, damaging them.");
                    lore.add(ChatColor.WHITE
                            + "Shift click: Emits a thunder sound effect and applies a glowing potion effect to nearby entities, excluding the player.");
                    lore.add(ChatColor.WHITE + "Left click: Launches the player forward in the direction rail.");
                    break;
                case 7:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE
                            + "Right click: Weakens the target player, reducing their strength temporarily.");
                    lore.add(ChatColor.WHITE
                            + "Shift click: Engulfs the target player in darkness, impairing their vision and movement.");
                    lore.add(ChatColor.WHITE
                            + "Left click: Creates a sand block temporarily that slows enemies passing on it.");
                    break;
                case 8:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE + "Right click: Throw an ice block, dealing damage to whoever gets hit");
                    lore.add(ChatColor.WHITE + "Shift click: Spawns snow golems to fight for you");
                    lore.add(ChatColor.WHITE + "Left click: Freezes the player you aim giving him slowness");
                    break;
                case 9:
                    lore.add(ChatColor.GREEN + "Abilities");
                    lore.add(ChatColor.WHITE + "Right click: Make a wall of lava");
                    lore.add(ChatColor.WHITE + "Shift click: Spawn a blaze to fight for you");
                    lore.add(ChatColor.WHITE + "Left click: 1 minute of Fire resistance");
                    break;
                default:
                    l.warning("There was an error creating a gem, please inform the developer.");
                    l.warning("Gem number: " + gemNumber + " on lore creation");
                    break;
            }
        }
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
     * @param gemNumber The number representing the gem.
     * @return A string representing the color code.
     */
    private String getColor(int gemNumber) {
        if (cm.isRandomizedColors()) {
            return possibleColors.get(rand.nextInt(possibleColors.size())).toString();
        }
        return switch (gemNumber) {
            case 1 -> ChatColor.DARK_GREEN + "";
            case 2 -> ChatColor.LIGHT_PURPLE + "";
            case 3 -> ChatColor.WHITE + "";
            case 4 -> ChatColor.RED + "";
            case 5 -> ChatColor.GRAY + "";
            case 6 -> ChatColor.YELLOW + "";
            case 7 -> ChatColor.GOLD + "";
            case 8 -> ChatColor.AQUA + "";
            case 9 -> ChatColor.DARK_RED + "";
            default -> ChatColor.BLACK + "";
        };
    }

    /**
     * Generates an ItemStack for a gem.
     * 
     * @param gemNumber The power of the gem.
     * @param gemLevel  The level of the gem.
     * @return An ItemStack representing the created gem.
     */
    private ItemStack generateItemStack(int gemNumber, int gemLevel) {
        ItemStack holderItem = new ItemStack(Material.EMERALD);
        ItemMeta reGemMeta = holderItem.getItemMeta();
        ItemStack finalGem = new ItemStack(Material.EMERALD);
        reGemMeta.setDisplayName(getColor(gemNumber) + lookUpName(gemNumber) + " Gem");
        PersistentDataContainer reDataContainer = reGemMeta.getPersistentDataContainer();
        reDataContainer.set(isGemKey, PersistentDataType.BOOLEAN, true);
        reDataContainer.set(gemPowerKey, PersistentDataType.STRING, lookUpName(gemNumber));
        reDataContainer.set(gemLevelKey, PersistentDataType.INTEGER, gemLevel);
        reGemMeta = createLore(reGemMeta, gemNumber);
        reGemMeta.setCustomModelData(gemNumber);
        finalGem.setItemMeta(reGemMeta);
        return finalGem;
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
        for (int i = 1; i <= 10; i++) {
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
        ArrayList<ItemStack> foundGems = new ArrayList<>(1);
        Arrays.stream(plr.getInventory().getContents().clone()).filter(this::isGem).forEach(foundGems::add);
        return foundGems;
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
     * @return The Class object representing the gem's class, or null if the item is
     *         not a gem.
     */
    public Class<?> getGemClass(ItemStack item) {
        if (!isGem(item))
            return null;
        try {
            return Class.forName("me.iseal.powergems.gems."
                    + item.getItemMeta().getPersistentDataContainer().get(gemPowerKey, PersistentDataType.STRING)
                    + "Gem");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the name of a gem.
     * If the item is not a gem, returns null.
     * 
     * @param item The ItemStack to check.
     * @return The name of the gem, or null if the item is not a gem.
     */
    public String getGemName(ItemStack item) {
        if (!isGem(item))
            return null;
        return item.getItemMeta().getPersistentDataContainer().get(gemPowerKey, PersistentDataType.STRING);
    }

    /**
     * Runs a method call on a gem.
     * If the item is not a gem, throws an IllegalArgumentException.
     * 
     * @param item   The ItemStack representing the gem.
     * @param action The Action to perform.
     * @param plr    The player performing the action.
     */
    public void runCall(ItemStack item, Action action, Player plr) {
        if (!isGem(item))
            throw new IllegalArgumentException("Item is not a gem");
        try {
            Class<?> classObj = getGemClass(item);
            Object instance = classObj.getDeclaredConstructor().newInstance();
            Method init = classObj.getMethod("call", Action.class, Player.class, ItemStack.class);
            init.invoke(instance, action, plr, item);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Something went wrong with the dial init");
        }
    }

}
