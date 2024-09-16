package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;
import dev.iseal.powergems.misc.ExceptionHandler;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.logging.Level;

public class GemLoreConfigManager extends AbstractConfigManager {

    public GemLoreConfigManager() {
        super("GemLore");
    }

    @Override
    public void setUpConfig() {
        for (int i = 1; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            createDefaultLore(i);
        }
    }

    public void createDefaultLore(int gemNumber) {
        if (file.contains("Gem" + gemNumber + "Lore"))
            return;
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        switch (gemNumber) {
            case 1:
                lore.add(ChatColor.WHITE + "Right click: Saturation, Strength and Resistance (all lvl 2)");
                lore.add(ChatColor.WHITE + "Shift click: An arena that keeps anyone from entering, useful to heal");
                lore.add(ChatColor.WHITE + "Left click: A shockwave that sends everyone near flying and damages them");
            break;
            case 2:
                lore.add(ChatColor.WHITE + "Right click: Parry");
                lore.add(ChatColor.WHITE + "Shift click: Instant heal");
                lore.add(ChatColor.WHITE + "Left click: 1 minute of regeneration 2");
            break;
            case 3:
                lore.add(ChatColor.WHITE
                        + "Right click: Creates a tether of wind between the player and a target player, pulling the target closer.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Creates a cloud of smoke, granting temporary invisibility and propelling the player forward.");
                lore.add(ChatColor.WHITE
                        + "Left click: Unleashes a burst of wind, launching nearby entities into the air and dealing damage.");
            break;
            case 4:
                lore.add(ChatColor.WHITE
                        + "Right click: Creates a fiery aura around the player, granting fire resistance and igniting nearby air blocks.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Triggers a powerful explosion at the player's location, damaging nearby entities and applying fire damage.");
                lore.add(ChatColor.WHITE
                        + "Left click: Launches a fireball in the direction the player is facing, causing an explosion upon impact.");
            break;
            case 5:
                lore.add(ChatColor.WHITE
                        + "Right click: Temporarily grants the player increased absorption and knockback resistance.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Temporarily increases the player's armor and armor toughness.");
                lore.add(ChatColor.WHITE + "Left click: Fires a barrage of spectral arrows in a circle shape.");
            break;
            case 6:
                lore.add(ChatColor.WHITE
                        + "Right click: Strikes lightning at the target location and nearby entities, damaging them.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Emits a thunder sound effect and applies a glowing potion effect to nearby entities, excluding the player.");
                lore.add(ChatColor.WHITE + "Left click: Launches the player forward in the direction rail.");
            break;
            case 7:
                lore.add(ChatColor.WHITE
                        + "Right click: Weakens the target player, reducing their strength temporarily.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Engulfs the target player in darkness, impairing their vision and movement.");
                lore.add(ChatColor.WHITE
                        + "Left click: Creates a sand block temporarily that slows enemies passing on it.");
            break;
            case 8:
                lore.add(ChatColor.WHITE + "Right click: Throw an ice block, dealing damage to whoever gets hit");
                lore.add(ChatColor.WHITE + "Shift click: Spawns snow golems to fight for you");
                lore.add(ChatColor.WHITE + "Left click: Freezes the player you aim giving him slowness");
            break;
            case 9:
                lore.add(ChatColor.WHITE + "Right click: Make a wall of lava");
                lore.add(ChatColor.WHITE + "Shift click: Spawn a blaze to fight for you");
                lore.add(ChatColor.WHITE + "Left click: 1 minute of Fire resistance");
            break;
            case 10:
                lore.add(ChatColor.WHITE + "Right click: Propel yourself forward in water, creating bubbles.");
                lore.add(ChatColor.WHITE + "Shift click: Create a temporary water cube around you, granting Dolphin's Grace.");
                lore.add(ChatColor.WHITE + "Left click: Moisturize farmland blocks around you.");
                lore.add(ChatColor.BLUE + "Passive: Power up yourself with water");
            break;
            default:
                ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid gem number"), Level.WARNING, "ERROR_ON_LORE_CREATION", gemNumber);
            break;
        }
        file.setDefault("Gem" + GemManager.lookUpName(gemNumber) + "Lore", lore);
    }

    public ArrayList<String> getLore(int gemNumber) {
        return (ArrayList<String>) file.getStringList("Gem" + GemManager.lookUpName(gemNumber) + "Lore");
    }

}
