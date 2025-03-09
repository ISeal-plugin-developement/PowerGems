package dev.iseal.powergems.managers;

import de.leonhard.storage.Yaml;
import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.sealLib.Utils.ExceptionHandler;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecipeManager implements Listener {

    private GemManager gemManager = null;
    private final Yaml recipes = new Yaml("recipes", PowerGems.getPlugin().getDataFolder() + "\\config\\");
    private GeneralConfigManager gcm = null;
    private NamespacedKeyManager nkm = null;
    private final Logger l = Bukkit.getLogger();

    private static RecipeManager instance = null;
    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
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

        if (e.getSlotType() == InventoryType.SlotType.RESULT && e.getCursor() != null && gemManager.isGem(e.getCursor())) {
            CraftingInventory ci = (CraftingInventory) e.getInventory();
            ItemStack[] matrix = ci.getMatrix().clone();
            for (int j = 0; j < 9; j++) {
                if (matrix[j] != null) {
                    matrix[j].setAmount(matrix[j].getAmount() - 1);
                    if (matrix[j].getAmount() <= 0) {
                        matrix[j] = null;
                    }
                }
            }
            ci.setMatrix(matrix);
        }

        tryRandomGemCrafting(e);
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
                } else {
                    wantedMatrix[i] = new ItemStack(Material.getMaterial(ingredients.get(String.valueOf(c))));
                }
                i++;
            }
        }

        for (int j = 0; j < 9; j++) {
            if (j == gemIndex) {
                if (!gemManager.areGemsEqual(matrix[j], wantedMatrix[j])) {
                    return false;
                }
            } else if (wantedMatrix[j] == null || matrix[j] == null || !wantedMatrix[j].equals(matrix[j])) {
                return false;
            }
        }
        return true;
    }

    private void tryRandomGemCrafting(InventoryClickEvent e) {
        CraftingInventory ci = (CraftingInventory) e.getInventory();
        if (ci.getResult() == null) {
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
                int gemsFound = 0;
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
                    gemsFound++;
                    oldestGemCreationTime = gemManager.getGemCreationTime(is);
                }

                //Also check offhand
                ItemStack offhand = plr.getInventory().getItemInOffHand();
                if (gemManager.isGem(offhand) && gemManager.getGemCreationTime(offhand) < oldestGemCreationTime) {
                    index = -2;
                    gemsFound++;
                }

                if (gemsFound > 2) {
                    // how do i deal with this
                    //TODO: implement a way to allow only 1 actual gem. hard to trigger unless server changed config recently.
                }

                if (index == -1) {
                    throw new RuntimeException("Player has multiple gems but oldestGemCreationTime < -1 ???!?!?!?");
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
                sr.setIngredient(entry.getKey().charAt(0), Material.getMaterial(entry.getValue()));
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
                for (int level = 2; level <= 5; level++) {
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
                        sr.setIngredient(entry.getKey().charAt(0), Material.getMaterial(entry.getValue()));
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