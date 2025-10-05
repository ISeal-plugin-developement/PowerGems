package dev.iseal.powergems.managers;

import de.leonhard.storage.Yaml;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeManager implements Listener {

    private GemManager gemManager = null;
    private final Yaml recipes = new Yaml("recipes", PowerGems.getPlugin().getDataFolder() + "\\config\\");
    private GeneralConfigManager gcm = null;
    private NamespacedKeyManager nkm = null;
    private final Logger l = Bukkit.getLogger();

    // Material cache to avoid repeated Material.getMaterial calls
    private final Map<String, Material> materialCache = new ConcurrentHashMap<>();

    private static RecipeManager instance = null;
    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    // Helper method to get Material from cache or load it if not present
    private Material getCachedMaterial(String materialName) {
        return materialCache.computeIfAbsent(materialName, Material::getMaterial);
    }

    public void initiateRecipes() {
        gemManager = SingletonManager.getInstance().gemManager;
        gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        nkm = SingletonManager.getInstance().namespacedKeyManager;
        if (gcm.canUpgradeGems()) {
            l.info("[PowerGems] " + I18N.translate("CREATING_UPGRADE_RECIPES"));
            validateUpgradeRecipes();
            l.info("[PowerGems] " + I18N.translate("UPGRADE_RECIPES_CREATED"));
        }
        if (gcm.canCraftGems()) {
            l.info("[PowerGems] " + I18N.translate("CREATING_CRAFTING_RECIPE"));
            craftRecipe();
            l.info("[PowerGems] " + I18N.translate("CRAFTING_RECIPE_CREATED"));
        }
    }

    @EventHandler
    public void onGemCraftingAttempt(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.WORKBENCH) {
            return;
        }

        // Check if the player is clicking on a random gem result
        if (e.getSlotType() == InventoryType.SlotType.RESULT && e.getCurrentItem() != null && 
            e.getCurrentItem().isSimilar(gemManager.getRandomGemItem())) {
            // Cancel the vanilla event to handle it ourselves
            e.setCancelled(true);
            
            CraftingInventory ci = (CraftingInventory) e.getInventory();
            HumanEntity player = e.getWhoClicked();
            
            // Create a new specific gem instead of the random one
            String[] excludedTypes = collectOwnedGemNames(player.getInventory().getContents()).toArray(new String[0]);
            ItemStack newGem = gemManager.createGem(excludedTypes);
            
            // Add the new gem to player's inventory
            HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(newGem);
            if (!notAdded.isEmpty()) {
                // If inventory is full, drop the item at player's location
                for (ItemStack item : notAdded.values()) {
                    player.getWorld().dropItem(player.getLocation(), item);
                }
            }
            
            // Consume one item from each slot in the crafting grid
            ItemStack[] matrix = ci.getMatrix();
            for (int i = 0; i < matrix.length; i++) {
                if (matrix[i] != null) {
                    if (matrix[i].getAmount() > 1) {
                        matrix[i].setAmount(matrix[i].getAmount() - 1);
                    } else {
                        matrix[i] = null;
                    }
                }
            }
            ci.setMatrix(matrix);
            
            // Play crafting sound for feedback
            player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.BLOCK_ANVIL_USE, 0.5f, 1.0f);
            
            return; // Skip other processing since we handled this event
        }

        // Handle when player takes an item from the result slot for gem upgrades
        if (e.getSlotType() == InventoryType.SlotType.RESULT && e.getCurrentItem() != null) {
            CraftingInventory ci = (CraftingInventory) e.getInventory();

            // Handle gem upgrade result
            if (gemManager.isGem(e.getCurrentItem()) && !e.getCurrentItem().isSimilar(gemManager.getRandomGemItem())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(PowerGems.getPlugin(), () -> {
                    ItemStack[] matrix = ci.getMatrix();

                    for (int i = 0; i < matrix.length; i++) {
                        if (matrix[i] != null) {
                            if (matrix[i].getAmount() > 1) {
                                matrix[i].setAmount(matrix[i].getAmount() - 1);
                            } else {
                                matrix[i] = null;
                            }
                        }
                    }

                    ci.setMatrix(matrix);
                }, 1L);
            }
        }

        // Try to update the result slot with a gem upgrade if possible
        tryUpgradeCrafting(e);
    }

    private void tryUpgradeCrafting(InventoryClickEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(PowerGems.getPlugin(), () -> {
            CraftingInventory ci = (CraftingInventory) e.getInventory();
            if (!Objects.equals(ci.getResult(), null))
                return;
            ItemStack[] matrix = ci.getMatrix().clone();
            ItemStack gem = null;
            for (ItemStack is : matrix) {
                if (gemManager.isGem(is) && gemManager.getLevel(is) < gcm.getMaxGemLevel()) {
                    gem = new ItemStack(is.clone());
                    break;
                }
            }
            if (gem == null) {
                return;
            }
            int currentLevel = gemManager.getLevel(gem);
            if (isMatrixCorrect(matrix, gem, currentLevel + 1)) {
                ItemMeta im = gem.getItemMeta();
                PersistentDataContainer pdc = im.getPersistentDataContainer();
                pdc.set(nkm.getKey("gem_level"), PersistentDataType.INTEGER, currentLevel + 1);
                im = gemManager.createLore(im);
                gem.setItemMeta(im);
                ci.setResult(gem);
            }
        }, 1);
    }

    private boolean isMatrixCorrect(ItemStack[] matrix, ItemStack gem, int level) {
        String key = gemManager.getName(gem).toLowerCase() + "_" + level + "_upgrade";
        ItemStack[] wantedMatrix = new ItemStack[9];
        HashMap<String, Object> arr = (HashMap<String, Object>) recipes.getMap(key);
        String[] shape = arr.get("shape").toString().split(",");
        Map<String, String> ingredients = (Map<String, String>) arr.get("ingredients");

        // Populate wantedMatrix
        int i = 0;
        int gemIndex = -1;
        for (String s : shape) {
            for (char c : s.toCharArray()) {
                if (c == 'g') {
                    wantedMatrix[i] = gem;
                    gemIndex = i;
                } else if (ingredients.containsKey(String.valueOf(c))) {
                    Material material = getCachedMaterial(ingredients.get(String.valueOf(c)));
                    if (material != null) {
                        wantedMatrix[i] = new ItemStack(material);
                    }
                }
                i++;
            }
        }

        for (int j = 0; j < 9; j++) {
            // If this position has no requirement in the recipe
            if (wantedMatrix[j] == null) {
                if (matrix[j] != null) {
                    return false;  // Recipe requires an empty slot here
                }
                continue;
            }

            // If this position has a requirement but matrix is empty
            if (matrix[j] == null) {
                return false;
            }

            if (j == gemIndex) {
                // Check gem specifically since it needs special comparison
                if (!gemManager.areGemsEqual(matrix[j], wantedMatrix[j])) {
                    return false;
                }
            } else {
                // For other ingredients, just check if the material type matches
                // This ignores the quantity requirement
                if (matrix[j].getType() != wantedMatrix[j].getType()) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<String> collectOwnedGemNames(ItemStack[] contents) {
        List<String> ownedGemNames = new ArrayList<>();
        for (ItemStack is : contents) {
            if (gemManager.isGem(is)) {
                String gemName = gemManager.getName(is);
                if (gemName != null && !ownedGemNames.contains(gemName)) {
                    ownedGemNames.add(gemName);
                }
            }
        }
        return ownedGemNames;
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
                l.info("[PowerGems] " + I18N.translate("CRAFT_SHAPE_NOT_FOUND"));
                changed = true;
            }
            if (!arr.containsKey("ingredients")) {
                HashMap<String, String> defaultIngredients = new HashMap<>();
                defaultIngredients.put("n", "NETHERITE_BLOCK");
                defaultIngredients.put("g", "NETHER_STAR");
                defaultIngredients.put("d", "DIAMOND_BLOCK");
                arr.put("ingredients", defaultIngredients);
                l.info("[PowerGems] " + I18N.translate("CRAFT_INGREDIENTS_NOT_FOUND"));
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
                Material material = getCachedMaterial(entry.getValue());
                if (material != null) {
                    sr.setIngredient(entry.getKey().charAt(0), material);
                }
            }
            Bukkit.getServer().addRecipe(sr);
        } catch (Exception e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.SEVERE, "RECIPE_REGISTER_CRAFT");
        }
    }

    private void validateUpgradeRecipes() {
        String key = "";
        try {
            ItemStack oldStack;
            ItemStack newStack;
            for (ItemStack i : gemManager.getAllGems().values()) {
                oldStack = i;
                for (int level = 2; level <= gcm.getMaxGemLevel(); level++) {
                    newStack = oldStack.clone();
                    ItemMeta im = newStack.getItemMeta();
                    PersistentDataContainer pdc = im.getPersistentDataContainer();
                    pdc.set(nkm.getKey("gem_level"), PersistentDataType.INTEGER, level);
                    im = gemManager.createLore(im);
                    newStack.setItemMeta(im);
                    // generate namespacedkey based on name+level
                    key = gemManager.getName(newStack).toLowerCase()  + "_" + level + "_upgrade";
                    NamespacedKey nk = new NamespacedKey(PowerGems.getPlugin(), key);
                    ShapedRecipe sr = new ShapedRecipe(nk, newStack);
                    HashMap<String, Object> arr = (HashMap<String, Object>) recipes.getMap(key);

                    boolean changed = false;
                    if (!arr.containsKey("shape")) {
                        arr.put("shape", "nen,ege,nen");
                        changed = true;
                        l.info("[PowerGems] " + I18N.translate("SHAPE_NOT_FOUND_KEY").replace("{key}", key));
                    }
                    if (!arr.containsKey("ingredients")) {
                        HashMap<String, String> defaultIngredients = new HashMap<>();
                        defaultIngredients.put("n", Material.NETHERITE_INGOT.name());
                        defaultIngredients.put("e", Material.EXPERIENCE_BOTTLE.name());
                        arr.put("ingredients", defaultIngredients);
                        changed = true;
                        l.info("[PowerGems] " + I18N.translate("INGREDIENTS_NOT_FOUND_KEY").replace("{key}", key));
                    }

                    if (changed)
                        // Save the changes to the recipes Yaml file
                        recipes.set(key, arr);

                    String[] shape = arr.get("shape").toString().split(",");
                    sr.shape(shape[0], shape[1], shape[2]);
                    Map<String, String> ingredients = (Map<String, String>) arr.get("ingredients");

                    for (Map.Entry<String, String> entry : ingredients.entrySet()) {
                        Material material = getCachedMaterial(entry.getValue());
                        if (material != null) {
                            sr.setIngredient(entry.getKey().charAt(0), material);
                        }
                    }

                    if (!arr.get("shape").toString().contains("g"))
                        throw new RuntimeException("No gem ingredient found for " + key + " (is the file malformed?)");

                    sr.setIngredient('g', new RecipeChoice.ExactChoice(oldStack));
                    // Register the recipe to test the validity
                    Bukkit.getServer().addRecipe(sr);
                    Bukkit.getServer().removeRecipe(nk);
                    oldStack = newStack;
                    key = "";
                }
            }
        } catch (Exception e) {
            ExceptionHandler.getInstance().dealWithException(e, Level.SEVERE, "RECIPE_REGISTER_UPGRADE", key);
        }
    }
}
