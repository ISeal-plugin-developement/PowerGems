package me.iseal.powergems.managers;

import de.leonhard.storage.Yaml;
import me.iseal.powergems.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RecipeManager implements Listener {

    private GemManager gemManager = null;
    private final Yaml recipes = new Yaml("recipes", Main.getPlugin().getDataFolder()+"\\config\\");
    private final Logger l = Bukkit.getLogger();
    
    public void initiateRecipes(){
        gemManager = Main.getSingletonManager().gemManager;
        if (Main.config.getBoolean("canUpgradeGems")) {
            upgradeRecipe();
        }
        if (Main.config.getBoolean("canCraftGems")){
            craftRecipe();
        }
    }

    @EventHandler
    public void onItemPickup(InventoryClickEvent e){
        if (e.getInventory().getType() != InventoryType.WORKBENCH){
            return;
        }
        if (!e.getInventory().contains(Material.NETHERITE_BLOCK)){
            return;
        }
        ItemStack i = e.getInventory().getItem(0);
        if (i == null){
            return;
        }
        if (!i.hasItemMeta()){
            return;
        }
        if (!i.getItemMeta().getPersistentDataContainer().has(Main.getIsRandomGemKey(), PersistentDataType.BYTE)){
            return;
        }
        if (e.getCurrentItem() == null){
            return;
        }
        if (!e.getCurrentItem().isSimilar(gemManager.getRandomGemItem())){
            return;
        }
        if (Main.config.getBoolean("allowOnlyOneGem")) {
            for (ItemStack is : e.getWhoClicked().getInventory().getContents()) {
              if (gemManager.isGem(is)) {
                    e.getWhoClicked().getInventory().remove(is);
                }
            }
        }
        e.setCurrentItem(gemManager.createGem());
    }

    private void craftRecipe(){
        try {
            String key = "gem_craft_recipe";
            NamespacedKey nk = new NamespacedKey(Main.getPlugin(), key);
            ShapedRecipe sr = new ShapedRecipe(nk, Main.getSingletonManager().gemManager.getRandomGemItem());
            HashMap<String, Object> arr = (HashMap<String, Object>) recipes.getMap("gem_craft_recipe");
            if (!arr.containsKey("shape")) {
                arr.put("shape", "nen,ege,nen");
                l.info("Shape not found for crafting (is the file malformed?), using default shape.");
            }
            if (!arr.containsKey("ingredients")) {
                HashMap<String, String> defaultIngredients = new HashMap<>();
                defaultIngredients.put("n", "NETHERITE_BLOCK");
                defaultIngredients.put("e", "NETHER_STAR");
                defaultIngredients.put("g", "DIAMOND_BLOCK");
                arr.put("ingredients", defaultIngredients);
                l.info("Ingredients not found for crafting (is the file malformed?), using default ingredients.");
            }

            // Save the changes to the recipes Yaml file
            recipes.set("gem_craft_recipe", arr);
            String[] shape = arr.get("shape").toString().split(",");
            sr.shape(shape[0], shape[1], shape[2]);
            Map<String, String> ingredients = (Map<String, String>) arr.get("ingredients");
            for (Map.Entry<String, String> entry : ingredients.entrySet()) {
                sr.setIngredient(entry.getKey().charAt(0), Material.getMaterial(entry.getValue()));
            }
            Bukkit.getServer().addRecipe(sr);
        } catch (Exception e){
            l.severe("Error while creating gem crafting recipe, check the configuration file.");
            l.severe("Disabling plugin to prevent errors.");
            l.severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
        }
    }

    private void upgradeRecipe(){
        String key = "";
        try {
            ItemStack oldStack;
            ItemStack newStack;
            for (ItemStack i : gemManager.getAllGems().values()) {
                oldStack = i;
                for (int level = 2; level <= 5; level++) {
                    newStack = oldStack.clone();
                    ItemMeta im = newStack.getItemMeta();
                    PersistentDataContainer pdc = im.getPersistentDataContainer();
                    pdc.set(Main.getGemLevelKey(), PersistentDataType.INTEGER, level);
                    im = gemManager.createLore(im);
                    newStack.setItemMeta(im);
                    //generate namespacedkey based on name+level
                    key = generateName(im.getDisplayName()) + "_" + level + "_upgrade";
                    NamespacedKey nk = new NamespacedKey(Main.getPlugin(), key);
                    ShapedRecipe sr = new ShapedRecipe(nk, newStack);
                    HashMap<String, Object> arr = (HashMap<String, Object>) recipes.getMap(key);
                    if (!arr.containsKey("shape")) {
                        arr.put("shape", "nen,ege,nen");
                        l.info("Shape not found for "+key+" (is the file malformed?), using default shape.");
                    }
                    if (!arr.containsKey("ingredients")) {
                        HashMap<String, String> defaultIngredients = new HashMap<>();
                        defaultIngredients.put("n", Material.NETHERITE_INGOT.name());
                        defaultIngredients.put("e", Material.EXPERIENCE_BOTTLE.name());
                        arr.put("ingredients", defaultIngredients);
                        l.info("Ingredients not found for "+key+" (is the file malformed?), using default ingredients.");
                    }

                    // Save the changes to the recipes Yaml file
                    recipes.set(key, arr);
                    String[] shape = arr.get("shape").toString().split(",");
                    char[] shapeChars = shape[1].toCharArray();
                    shapeChars[1] = 'g';
                    shape[1] = String.valueOf(shapeChars);
                    sr.shape(shape[0], shape[1], shape[2]);
                    Map<String, String> ingredients = (Map<String, String>) arr.get("ingredients");
                    for (Map.Entry<String, String> entry : ingredients.entrySet()) {
                        if (entry.getKey().equals("g")) {
                            continue;
                        }
                        sr.setIngredient(entry.getKey().charAt(0), Material.getMaterial(entry.getValue()));
                    }
                    sr.setIngredient('g', new RecipeChoice.ExactChoice(oldStack));
                    Bukkit.getServer().addRecipe(sr);
                /*sr.shape("nen","ege","nen");
                sr.setIngredient('n', Material.NETHERITE_INGOT);
                sr.setIngredient('e', Material.EXPERIENCE_BOTTLE);
                sr.setIngredient('g', new RecipeChoice.ExactChoice(oldStack));
                Bukkit.getServer().addRecipe(sr);*/
                    oldStack = newStack;
                    key = "";
                }
            }
        } catch (Exception e){
            l.severe("Error while creating gem upgrade recipes, check the configuration file.");
            l.severe("Last key: "+key);
            l.severe("Disabling plugin to prevent errors.");
            l.severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(Main.getPlugin());
        }
    }
    private static String generateName(String s){
        s = s.replace(" ", "_");
        StringBuilder finalString = new StringBuilder();
        boolean lastWas = false;
        ArrayList<Character> characterList = (ArrayList<Character>) s.chars().mapToObj(c -> (char)c).collect(Collectors.toList());

        for (int i = 0; i < characterList.size(); i++) {
            if (characterList.get(i).toString().equals("§")){
                lastWas = true;
                continue;
            }
            if (lastWas){
                lastWas = false;
                continue;
            }
            finalString.append(characterList.get(i));
        }
        return finalString.toString().toLowerCase();
    }
}
