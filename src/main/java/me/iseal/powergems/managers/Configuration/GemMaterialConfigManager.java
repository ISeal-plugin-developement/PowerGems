package me.iseal.powergems.managers.Configuration;

import de.leonhard.storage.Config;
import me.iseal.powergems.Main;
import me.iseal.powergems.managers.GemManager;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.BatchUpdateException;
import java.util.ArrayList;

public class GemMaterialConfigManager extends AbstractConfigManager {

    private final GemManager gemManager = SingletonManager.getInstance().gemManager;
    private final ArrayList<Material> possibleMaterials = new ArrayList<>();

    @Override
    public void setUpConfig() {
        file = new Config("gemMaterials", Main.getPlugin().getDataFolder() + "\\config\\");
        file.setDefault("RandomGemMaterial", "EMERALD");
        gemManager.getAllGems().values().forEach(gem -> {
            file.setDefault(gemManager.getGemName(gem) + "Material", gem.getType().name());
        });
    }

    /*
        * Get the material of a gem
        *
        * @param item The item to get the gem material from
        * @return The material of the gem
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
            Bukkit.getLogger().severe(SingletonManager.getInstance().configManager.getGeneralConfigManager().getPluginPrefix()+" Invalid material for gem " + name + ". Defaulting to EMERALD");
            return Material.EMERALD;
        }
    }

    public Material getRandomGemMaterial() {
        try {
            return Material.valueOf(file.getString("RandomGemMaterial"));
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe(SingletonManager.getInstance().configManager.getGeneralConfigManager().getPluginPrefix()+" Invalid material for random gem. Defaulting to EMERALD");
            return Material.EMERALD;
        }
    }

}
