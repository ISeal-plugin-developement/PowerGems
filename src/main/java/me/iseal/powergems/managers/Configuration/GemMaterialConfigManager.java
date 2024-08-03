package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import me.iseal.powergems.Main;
import me.iseal.powergems.managers.ConfigManager;
import me.iseal.powergems.managers.GemManager;
import me.iseal.powergems.managers.SingletonManager;
import me.iseal.powergems.misc.AbstractConfigManager;
import me.iseal.powergems.misc.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.internal.Logger;

import java.util.ArrayList;
import java.util.logging.Level;

public class GemMaterialConfigManager extends AbstractConfigManager {

    private GemManager gemManager = null;
    private GeneralConfigManager gcm = null;
    private final ArrayList<Material> possibleMaterials = new ArrayList<>();

    @Override
    public void setUpConfig() {
        file = new Config("gemMaterials", ConfigManager.getConfigFolderPath());
        file.setDefault("RandomGemMaterial", "EMERALD");
    }
    
    //Initialize after other classes have been instantiated
    public void lateInit() {
        gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        gemManager = SingletonManager.getInstance().gemManager;
        System.out.println("Executing!!");
        gemManager.getAllGems().values().forEach(gem -> {
            file.setDefault(gemManager.getGemName(gem)+"GemMaterial", gem.getType().toString());
            System.out.println(gemManager.getGemName(gem)+"GemMaterial");
        });
    }

    @Override
    public void reloadConfig() {
        file.forceReload();
        possibleMaterials.clear();
        for (String key : file.singleLayerKeySet()) {
            if (key.equals("RandomGemMaterial")) {
                continue;
            }
            if (gemManager.lookUpID(key) == -1) {
                Bukkit.getLogger().severe(gcm.getPluginPrefix() + "Invalid gem name in gemMaterials.yml: " + key+" Skipping...");
                continue;
            }
            if (!possibleMaterials.contains(Material.getMaterial(file.getString(key)))) {
                possibleMaterials.add(Material.getMaterial(file.getString(key)));
            }
        }
    }

    /*
        * Get the material of a gem
        *
        * @param item The item to get the gem material from
        * @return The material of the gem, null if the item is not a gem
     */
    public Material getGemMaterial(ItemStack item) {
        if (!gemManager.isGem(item))
            return null;
        return getGemMaterial(gemManager.getGemName(item));
    }

    /*
        * Get the material of a gem
        *
        * @param name The name of the gem, as given by GemManager#getGemName
        * @return The material of the gem
     */
    public Material getGemMaterial(String name) {
        try {
            return Material.valueOf(file.getString(name + "GemMaterial"));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe(gcm.getPluginPrefix() + "Invalid material for gem " + name + ". Defaulting to EMERALD");
            ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "INVALID_MATERIAL_IN_CONFIG", name, file.getString(name + "GemMaterial"));
            return Material.EMERALD;
        }
    }

    public Material getRandomGemMaterial() {
        try {
            return Material.valueOf(file.getString("RandomGemMaterial"));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe(gcm.getPluginPrefix() + "Invalid material for random gem. Defaulting to EMERALD");
            return Material.EMERALD;
        }
    }

    public ArrayList<Material> getPossibleMaterials() {
        while (possibleMaterials.isEmpty()) {
            //wait
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return possibleMaterials;
    }

}
