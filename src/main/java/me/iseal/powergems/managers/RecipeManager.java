package me.iseal.powergems.managers;

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
import java.util.stream.Collectors;

public class RecipeManager implements Listener {

    private GemManager gemManager = null;
    
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
            String key = "gem_craft_recipe";
            NamespacedKey nk = new NamespacedKey(Main.getPlugin(), key);
            ShapedRecipe sr = new ShapedRecipe(nk,Main.getSingletonManager().gemManager.getRandomGemItem());
            sr.shape("ndn","dgd","ndn");
            sr.setIngredient('n', Material.NETHERITE_BLOCK);
            sr.setIngredient('d', Material.DIAMOND_BLOCK);
            sr.setIngredient('g', Material.NETHER_STAR);
            Bukkit.getServer().addRecipe(sr);
    }

    private void upgradeRecipe(){
        ItemStack oldStack;
        ItemStack newStack;
        for (ItemStack i : gemManager.getAllGems().values()){
            oldStack = i;
            for (int level = 2; level <= 5; level++) {
                newStack = oldStack.clone();
                ItemMeta im = newStack.getItemMeta();
                PersistentDataContainer pdc = im.getPersistentDataContainer();
                pdc.set(Main.getGemLevelKey(), PersistentDataType.INTEGER, level);
                im = gemManager.createLore(im);
                newStack.setItemMeta(im);
                //generate namespacedkey based on name+level
                String key = generateName(im.getDisplayName())+"_"+level+"_upgrade";
                NamespacedKey nk = new NamespacedKey(Main.getPlugin(), key);
                ShapedRecipe sr = new ShapedRecipe(nk,newStack);
                sr.shape("nen","ege","nen");
                sr.setIngredient('n', Material.NETHERITE_INGOT);
                sr.setIngredient('e', Material.EXPERIENCE_BOTTLE);
                sr.setIngredient('g', new RecipeChoice.ExactChoice(oldStack));
                Bukkit.getServer().addRecipe(sr);
                oldStack = newStack;
            }
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
