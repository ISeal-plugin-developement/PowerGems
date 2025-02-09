package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GemMaterialConfigManager extends AbstractConfigManager{

    private GemManager gemManager = null;
    private GeneralConfigManager gcm = null;
    private final ArrayList<Material> possibleMaterials = new ArrayList<>();

    public GemMaterialConfigManager() {
        super("gemMaterials");
    }

    @Override
    public void setUpConfig() {
        file.setDefault("RandomGemMaterial", "EMERALD");
    }
    
    //Initialize after other classes have been instantiated
    public void lateInit() {
        gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
        gemManager = SingletonManager.getInstance().gemManager;
        gemManager.getAllGems().values().forEach(gem -> file.setDefault(gemManager.getGemName(gem)+"GemMaterial", gem.getType().toString()));
    }

    @Override
    public void reloadConfig() {
        file.forceReload();
        possibleMaterials.clear();
        for (String key : file.singleLayerKeySet()) {
            if (key.equals("RandomGemMaterial")) {
                continue;
            }
            if (GemManager.lookUpID(key.replace("GemMaterial", "")) == -1) {
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
        // Reuse gemManager to verify if item is a gem
        if (!gemManager.isGem(item)) {
            return null;
        }
        // Use gem’s internal name from GemManager (like gemManager.getGemName(item))
        String gemName = gemManager.getGemName(item);
        return getGemMaterial(gemName);
    }

    /*
        * Get the material of a gem
        *
        * @param name The name of the gem, as given by GemManager#getGemName
        * @return The material of the gem
     */
    public Material getGemMaterial(String gemName) {
        // Retrieve from config or default to EMERALD
        try {
            return Material.valueOf(file.getOrSetDefault(gemName + "GemMaterial", Material.EMERALD.name()));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe(gcm.getPluginPrefix() + "Invalid material for gem " + gemName + ". Defaulting to EMERALD");
            return Material.EMERALD;
        }
    }

    public Material getRandomGemMaterial() {
        try {
            return Material.valueOf(file.getOrSetDefault("RandomGemMaterial", Material.EMERALD.name()));
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
