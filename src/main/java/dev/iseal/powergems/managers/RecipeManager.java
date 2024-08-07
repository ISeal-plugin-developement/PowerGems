package dev.iseal.powergems.managers;

import de.leonhard.storage.Yaml;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.misc.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RecipeManager implements Listener {

    private GemManager gemManager = null;
    private final Yaml recipes = new Yaml("recipes", PowerGems.getPlugin().getDataFolder() + "\\config\\");
    private GeneralConfigManager gcm = null;
    private NamespacedKeyManager nkm = null;
    private final Logger l = Bukkit.getLogger();

    public void initiateRecipes() {
        gemManager = SingletonManager.getInstance().gemManager;
        gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        nkm = SingletonManager.getInstance().namespacedKeyManager;
        if (gcm.isRandomizedColors()){
            l.severe(gcm.getPluginPrefix()+"Randomized colors are enabled, recipes will not work. Either turn off randomized colors or disable recipes in the config.");
            return;
        }
        if (gcm.canUpgradeGems()) {
            l.info("Creating upgrade recipes...");
            upgradeRecipe();
            l.info("Upgrade recipes created.");
        }
        if (gcm.canCraftGems()) {
            l.info("Creating crafting recipe");
            craftRecipe();
            l.info("Crafting recipe created.");
        }
    }

    @EventHandler
    public void onItemPickup(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.WORKBENCH) {
            return;
        }
        if (!e.getInventory().contains(Material.NETHERITE_BLOCK)) {
            return;
        }
        ItemStack i = e.getInventory().getItem(0);
        if (i == null) {
            return;
        }
        if (!i.hasItemMeta()) {
            return;
        }
        if (!i.getItemMeta().getPersistentDataContainer().has(nkm.getKey("is_random_gem"), PersistentDataType.BYTE)) {
            return;
        }
        if (e.getCurrentItem() == null) {
            return;
        }
        if (!e.getCurrentItem().isSimilar(gemManager.getRandomGemItem())) {
            return;
        }
        HumanEntity plr = e.getWhoClicked();
        if (gcm.allowOnlyOneGem() && SingletonManager.getInstance().utils.hasAtLeastXAmountOfGems(plr.getInventory(), 1, plr.getInventory().getItemInOffHand())) {
            if (gcm.useNewAllowOnlyOneGemAlgorithm()){
                 long oldestGemCreationTime = -1;
                 int oldIndex = -1;
                 int index = -1;
                 for (ItemStack is : plr.getInventory().getContents()) {
                     index++;
                     if (!gemManager.isGem(is)){
                         continue;
                     }
                     if (gemManager.getGemCreationTime(is) > oldestGemCreationTime) {
                         continue;
                     }
                     //Gem is older
                     oldIndex = index;
                     oldestGemCreationTime = gemManager.getGemCreationTime(is);
                 }

                 //Also check offhand
                ItemStack offhand = plr.getInventory().getItemInOffHand();
                if (gemManager.isGem(offhand) && gemManager.getGemCreationTime(offhand) < oldestGemCreationTime) {
                    index = -2;
                }

                if (index == -1) {
                    throw new RuntimeException("Player has multiple gems but oldestGemCreationTime < -1 ???!?!");
                }
                if (index == -2) {
                    //its offhand
                    plr.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                } else {
                    plr.getInventory().setItem(oldIndex, new ItemStack(Material.AIR));
                }
            } else {
                for (ItemStack is : plr.getInventory().getContents()) {
                    if (gemManager.isGem(is)) {
                        plr.getInventory().remove(is);
                    }
                }
            }
        }
        e.setCurrentItem(gemManager.createGem());
    }

    private void craftRecipe() {
        try {
            String key = "gem_craft_recipe";
            NamespacedKey nk = new NamespacedKey(PowerGems.getPlugin(), key);
            ShapedRecipe sr = new ShapedRecipe(nk, SingletonManager.getInstance().gemManager.getRandomGemItem());
            HashMap<String, Object> arr = (HashMap<String, Object>) recipes.getMap("gem_craft_recipe");

            boolean changed = false;

            if (!arr.containsKey("shape")) {
                arr.put("shape", "ndn,dgd,ndn");
                l.info("Shape not found for crafting (is the file malformed?), using default shape.");
                changed = true;
            }
            if (!arr.containsKey("ingredients")) {
                HashMap<String, String> defaultIngredients = new HashMap<>();
                defaultIngredients.put("n", "NETHERITE_BLOCK");
                defaultIngredients.put("g", "NETHER_STAR");
                defaultIngredients.put("d", "DIAMOND_BLOCK");
                arr.put("ingredients", defaultIngredients);
                l.info("Ingredients not found for crafting (is the file malformed?), using default ingredients.");
                changed = true;
            }

            if (changed)
                // Save the changes to the recipes Yaml file
                recipes.set("gem_craft_recipe", arr);

            // Generate array with shape
            String[] shape = arr.get("shape").toString().split(",");
            sr.shape(shape[0], shape[1], shape[2]);
            Map<String, String> ingredients = (Map<String, String>) arr.get("ingredients");
            for (Map.Entry<String, String> entry : ingredients.entrySet()) {
                sr.setIngredient(entry.getKey().charAt(0), Material.getMaterial(entry.getValue()));
            }
            Bukkit.getServer().addRecipe(sr);
        } catch (Exception e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.SEVERE, "RECIPE_REGISTER_CRAFT");
        }
    }

    private void upgradeRecipe() {
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
                    pdc.set(nkm.getKey("gem_level"), PersistentDataType.INTEGER, level);
                    im = gemManager.createLore(im);
                    newStack.setItemMeta(im);
                    // generate namespacedkey based on name+level
                    key = generateName(im.getDisplayName()) + "_" + level + "_upgrade";
                    NamespacedKey nk = new NamespacedKey(PowerGems.getPlugin(), key);
                    ShapedRecipe sr = new ShapedRecipe(nk, newStack);
                    HashMap<String, Object> arr = (HashMap<String, Object>) recipes.getMap(key);

                    if (arr == null) {
                        throw new RuntimeException("Recipe map is null for key: " + key);
                    }

                    boolean changed = false;
                    if (!arr.containsKey("shape")) {
                        arr.put("shape", "nen,ege,nen");
                        changed = true;
                        l.info("Shape not found for " + key + " (is the file malformed?), using default shape.");
                    }
                    if (!arr.containsKey("ingredients")) {
                        HashMap<String, String> defaultIngredients = new HashMap<>();
                        defaultIngredients.put("n", Material.NETHERITE_INGOT.name());
                        defaultIngredients.put("e", Material.EXPERIENCE_BOTTLE.name());
                        arr.put("ingredients", defaultIngredients);
                        changed = true;
                        l.info("Ingredients not found for " + key + " (is the file malformed?), using default ingredients.");
                    }

                    if (changed)
                        // Save the changes to the recipes Yaml file
                        recipes.set(key, arr);

                    String[] shape = arr.get("shape").toString().split(",");
                    sr.shape(shape[0], shape[1], shape[2]);
                    Map<String, String> ingredients = (Map<String, String>) arr.get("ingredients");

                    for (Map.Entry<String, String> entry : ingredients.entrySet()) {
                        sr.setIngredient(entry.getKey().charAt(0), Material.getMaterial(entry.getValue()));
                    }

                    if (!arr.get("shape").toString().contains("g"))
                        throw new RuntimeException("No gem ingredient found for " + key + " (is the file malformed?)");

                    sr.setIngredient('g', new RecipeChoice.ExactChoice(oldStack));
                    Bukkit.getServer().addRecipe(sr);
                    oldStack = newStack;
                    key = "";
                }
            }
        } catch (Exception e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.SEVERE, "RECIPE_REGISTER_UPGRADE", key);
        }
    }

    private static String generateName(String s) {
        s = s.replace(" ", "_");
        StringBuilder finalString = new StringBuilder();
        boolean lastWas = false;
        ArrayList<Character> characterList = (ArrayList<Character>) s.chars().mapToObj(c -> (char) c)
                .collect(Collectors.toList());

        for (Character character : characterList) {
            if (character.toString().equals("§")) {
                lastWas = true;
                continue;
            }
            if (lastWas) {
                lastWas = false;
                continue;
            }
            finalString.append(character);
        }
        return finalString.toString().toLowerCase();
    }
}
