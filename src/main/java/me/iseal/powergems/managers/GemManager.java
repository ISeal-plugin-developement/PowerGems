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

public class GemManager {

    private ItemStack randomGem = null;
    private ConfigManager cm = null;
    private Random rand = new Random();
    private NamespacedKey isGemKey = null;
    private NamespacedKey gemPowerKey = null;
    private NamespacedKey gemLevelKey = null;
    private ArrayList<ChatColor> possibleColors = new ArrayList<>();

    public void initLater(){
        isGemKey = Main.getIsGemKey();
        gemPowerKey = Main.getGemPowerKey();
        gemLevelKey = Main.getGemLevelKey();
        possibleColors = removeElement(ChatColor.values(), ChatColor.MAGIC);
        cm = Main.getSingletonManager().configManager;
    }

    private ArrayList<ChatColor> removeElement(ChatColor[] originalArray, ChatColor elementToRemove) {
        ArrayList<ChatColor> list = new ArrayList<>(Arrays.asList(originalArray));
        list.removeIf(color -> color.equals(elementToRemove));
        return list;
    }

    public int lookUpID(String gemName){
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
            default -> -1;
        };
    }

    public String lookUpName(int gemID){
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
            default -> "Error";
        };
    }

    public boolean isGem(ItemStack is){
        if (is == null) return false;
        if (!is.hasItemMeta()) return false;
        if (is.getItemMeta().getPersistentDataContainer().has(isGemKey, PersistentDataType.BOOLEAN)) return true;
        return false;
    }

    public ItemStack getRandomGemItem(){
        if (randomGem == null){
            randomGem = new ItemStack(Material.EMERALD);
            ItemMeta gemMeta = randomGem.getItemMeta();
            gemMeta.setDisplayName(ChatColor.GREEN+"Random Gem");
            PersistentDataContainer pdc = gemMeta.getPersistentDataContainer();
            pdc.set(Main.getIsRandomGemKey(), PersistentDataType.BOOLEAN, true);
            randomGem.setItemMeta(gemMeta);
        }
        return randomGem;
    }

    public ItemStack createGem(int gemInt, int gemLvl){
        return generateItemStack(gemInt, gemLvl);
    }
    public ItemStack createGem(int gemInt){
        return generateItemStack(gemInt, 1);
    }
    public ItemStack createGem(String gemName, int gemLvl){
        return generateItemStack(lookUpID(gemName), gemLvl);
    }
    public ItemStack createGem(String gemName){
        return generateItemStack(lookUpID(gemName), 1);
    }
    public ItemStack createGem(){
        return generateItemStack(rand.nextInt(9) + 1, 1);
    }

    public ItemMeta createLore(ItemMeta meta, int gemNumber){
        if (!Main.config.getBoolean("gemsHaveDescriptions")){
            return meta;
        }
        ArrayList<String> lore = new ArrayList<>();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(gemLevelKey, PersistentDataType.INTEGER)) {
            pdc.set(gemLevelKey, PersistentDataType.INTEGER, 1);
        }
        lore.add(ChatColor.DARK_BLUE + "Level: " + ChatColor.DARK_GREEN + pdc.get(gemLevelKey, PersistentDataType.INTEGER));
        switch (gemNumber){
            case 1:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Saturation, Strenght and Resistance (all lvl 2)");
                lore.add(ChatColor.WHITE + "Shift click: An arena that keeps anyone from entering, useful to heal");
                lore.add(ChatColor.WHITE + "Left click: A shockwave that sends everyone near flying and damages them");
                break;
            case 2:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Parry");
                lore.add(ChatColor.WHITE + "Shift click: Instant heal");
                lore.add(ChatColor.WHITE + "Left click: 1 minute of regeneration 2");
                break;
            case 3:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Creates a tether of wind between the player and a target player, pulling the target closer.");
                lore.add(ChatColor.WHITE + "Shift click: Creates a cloud of smoke, granting temporary invisibility and propelling the player forward.");
                lore.add(ChatColor.WHITE + "Left click: Unleashes a burst of wind, launching nearby entities into the air and dealing damage.");
                break;
            case 4:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Creates a fiery aura around the player, granting fire resistance and igniting nearby air blocks.");
                lore.add(ChatColor.WHITE + "Shift click: Triggers a powerful explosion at the player's location, damaging nearby entities and applying fire damage.");
                lore.add(ChatColor.WHITE + "Left click: Launches a fireball in the direction the player is facing, causing an explosion upon impact.");
                break;
            case 5:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Temporarily grants the player increased absorption and knockback resistance.");
                lore.add(ChatColor.WHITE + "Shift click: Temporarily increases the player's armor and armor toughness.");
                lore.add(ChatColor.WHITE + "Left click: Fires a barrage of spectral arrows in a circle shape.");
                break;
            case 6:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Strikes lightning at the target location and nearby entities, damaging them.");
                lore.add(ChatColor.WHITE + "Shift click: Emits a thunder sound effect and applies a glowing potion effect to nearby entities, excluding the player.");
                lore.add(ChatColor.WHITE + "Left click: Launches the player forward in the direction rail.");
                break;
            case 7:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Weakens the target player, reducing their strength temporarily.");
                lore.add(ChatColor.WHITE + "Shift click: Engulfs the target player in darkness, impairing their vision and movement.");
                lore.add(ChatColor.WHITE + "Left click: Creates a sand block temporarily that slows enemies passing on it.");
                break;
            case 8:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Throw an ice block, dealing damage to whoever gets hit");
                lore.add(ChatColor.WHITE + "Shift click: Spawns snow golems to fight for you");
                lore.add(ChatColor.WHITE + "Left click: Freezes the player you aim giving him slowness");
                break;
            case 9:
                lore.add(ChatColor.GREEN+"Abilities");
                lore.add(ChatColor.WHITE + "Right click: Make a wall of lava");
                lore.add(ChatColor.WHITE + "Shift click: Spawn a blaze to fight for you");
                lore.add(ChatColor.WHITE + "Left click: 1 minute of Fire resistance");
                break;
            default:
                Bukkit.getLogger().warning("There was an error creating a gem, please inform the developer.");
                break;
        }
        meta.setLore(lore);
        return meta;
    }

    public ItemMeta createLore(ItemMeta meta){
        if (meta.getPersistentDataContainer().has(gemPowerKey, PersistentDataType.STRING)){
            return createLore(meta, lookUpID(meta.getPersistentDataContainer().get(gemPowerKey, PersistentDataType.STRING)));
        } else {
            return createLore(meta, 1);
        }
    }

    private String getColor(int gemNumber){
        if (cm.isRandomizedColors()){
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

    private ItemStack generateItemStack(int gemNumber, int gemLevel){
        ItemStack holderItem = new ItemStack(Material.EMERALD);
        ItemMeta reGemMeta = holderItem.getItemMeta();
        ItemStack finalGem = new ItemStack(Material.EMERALD);
        reGemMeta.setDisplayName(getColor(gemNumber)+ lookUpName(gemNumber)+" Gem");
        PersistentDataContainer reDataContainer = reGemMeta.getPersistentDataContainer();
        reDataContainer.set(isGemKey, PersistentDataType.BOOLEAN, true);
        reDataContainer.set(gemPowerKey, PersistentDataType.STRING, lookUpName(gemNumber));
        reDataContainer.set(gemLevelKey, PersistentDataType.INTEGER, gemLevel);
        reGemMeta = createLore(reGemMeta, gemNumber);
        reGemMeta.setCustomModelData(gemNumber);
        finalGem.setItemMeta(reGemMeta);
        return finalGem;
    }

    public HashMap<Integer, ItemStack> getAllGems(){
        HashMap<Integer, ItemStack> allGems = new HashMap<>(9);
        for (int i = 1; i <= 9; i++) {
            allGems.put(i,generateItemStack(i, 1));
        }
        return allGems;
    }

    public ArrayList<ItemStack> getPlayerGems(Player plr){
        ArrayList<ItemStack> foundGems = new ArrayList<>(1);
        Arrays.stream(plr.getInventory().getContents().clone()).filter(this::isGem).forEach(foundGems::add);
        return foundGems;
    }

    public int getLevel(ItemStack item) {
        if (!isGem(item)) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(gemLevelKey, PersistentDataType.INTEGER)) {
            pdc.set(gemLevelKey, PersistentDataType.INTEGER, 1);
        }
        return pdc.get(Main.getGemLevelKey(), PersistentDataType.INTEGER);
    }

    public Class<?> getGemClass(ItemStack item){
        if (!isGem(item)) return null;
        try {
            return Class.forName("me.iseal.powergems.gems."+item.getItemMeta().getPersistentDataContainer().get(gemPowerKey, PersistentDataType.STRING)+"Gem");
        } catch (Exception e){
            return null;
        }
    }

    public String getGemName(ItemStack item){
        if (!isGem(item)) return null;
        return item.getItemMeta().getPersistentDataContainer().get(gemPowerKey, PersistentDataType.STRING);
    }

    public void runCall(ItemStack item, Action action, Player plr){
        if (!isGem(item)) throw new IllegalArgumentException("Item is not a gem");
        try {
            Class<?> classObj = getGemClass(item);
            Object instance = classObj.getDeclaredConstructor().newInstance();
            Method init = classObj.getMethod("call", Action.class, Player.class, ItemStack.class);
            init.invoke(instance, action, plr, item);
        } catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("Something went wrong with the dial init");
        }
    }

}
