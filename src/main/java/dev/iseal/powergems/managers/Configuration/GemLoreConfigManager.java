package dev.iseal.powergems.managers.Configuration;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.GemReflectionManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.AbstractConfigManager;

import java.util.ArrayList;

public class GemLoreConfigManager extends AbstractConfigManager {

    public GemLoreConfigManager() {
        super("GemLore");
    }

    @Override
    public void setUpConfig() {
        file.setHeader(
                "This config file manages the lore of the gems",
                "The default lore is the same as the default abilities"
        );

    }

    @Override
    public void lateInit() {
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            createDefaultLore(GemManager.lookUpName(i));
        }
    }

    public void createDefaultLore(String gemName) {
        if (file.contains("Gem" + gemName + "Lore"))
            return;
        file.set(
                "Gem"+gemName+"Lore",
                GemReflectionManager.getInstance().getSingletonGemInstance(gemName).getDefaultLore()
        );
        /*
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Level %level%");
        lore.add(ChatColor.GREEN + "Abilities");
        switch (gemName) {
            case "Strength":
                lore.add(ChatColor.WHITE + "Right click: Saturation, Strength and Resistance (all lvl 2)");
                lore.add(ChatColor.WHITE + "Shift click: An arena that keeps anyone from entering, useful to heal");
                lore.add(ChatColor.WHITE + "Left click: A shockwave that sends everyone near flying and damages them");
            break;
            case "Healing":
                lore.add(ChatColor.WHITE + "Right click: Parry");
                lore.add(ChatColor.WHITE + "Shift click: Instant heal");
                lore.add(ChatColor.WHITE + "Left click: 1 minute of regeneration 2");
            break;
            case "Air":
                lore.add(ChatColor.WHITE
                        + "Right click: Creates a tether of wind between the player and a target player, pulling the target closer.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Creates a cloud of smoke, granting temporary invisibility and propelling the player forward.");
                lore.add(ChatColor.WHITE
                        + "Left click: Unleashes a burst of wind, launching nearby entities into the air and dealing damage.");
            break;
            case "Fire":
                lore.add(ChatColor.WHITE
                        + "Right click: Creates a fiery aura around the player, granting fire resistance and igniting nearby air blocks.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Triggers a powerful explosion at the player's location, damaging nearby entities and applying fire damage.");
                lore.add(ChatColor.WHITE
                        + "Left click: Launches a fireball in the direction the player is facing, causing an explosion upon impact.");
            break;
            case "Iron":
                lore.add(ChatColor.WHITE
                        + "Right click: Temporarily grants the player increased absorption and knockback resistance.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Temporarily increases the player's armor and armor toughness.");
                lore.add(ChatColor.WHITE + "Left click: Fires a barrage of spectral arrows in a circle shape.");
            break;
            case "Lightning":
                lore.add(ChatColor.WHITE
                        + "Right click: Strikes lightning at the target location and nearby entities, damaging them.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Emits a thunder sound effect and applies a glowing potion effect to nearby entities, excluding the player.");
                lore.add(ChatColor.WHITE + "Left click: Launches the player forward in the direction rail.");
            break;
            case "Sand":
                lore.add(ChatColor.WHITE
                        + "Right click: Weakens the target player, reducing their strength temporarily.");
                lore.add(ChatColor.WHITE
                        + "Shift click: Engulfs the target player in darkness, impairing their vision and movement.");
                lore.add(ChatColor.WHITE
                        + "Left click: Creates a sand block temporarily that slows enemies passing on it.");
            break;
            case "Ice":
                lore.add(ChatColor.WHITE + "Right click: Throw an ice block, dealing damage to whoever gets hit");
                lore.add(ChatColor.WHITE + "Shift click: Spawns snow golems to fight for you");
                lore.add(ChatColor.WHITE + "Left click: Freezes the player you aim giving him slowness");
            break;
            case "Lava":
                lore.add(ChatColor.WHITE + "Right click: Make a wall of lava");
                lore.add(ChatColor.WHITE + "Shift click: Spawn a blaze to fight for you");
                lore.add(ChatColor.WHITE + "Left click: 1 minute of Fire resistance");
            break;
            case "Water":
                lore.add(ChatColor.WHITE + "Right click: Propel yourself forward in water, creating bubbles.");
                lore.add(ChatColor.WHITE + "Shift click: Create a temporary water cube around you, granting Dolphin's Grace.");
                lore.add(ChatColor.WHITE + "Left click: Moisturize farmland blocks around you.");
                lore.add(ChatColor.BLUE + "Passive: Power up yourself with water");
            break;
            default:
                Bukkit.getLogger().log(Level.WARNING, "Unrecognized gem name: " + gemName+", if this looks like a gem from an addon you probably have to configure the lore yourself.");
            break;
        }
        file.set("Gem" + gemName + "Lore", lore);
         */

    }

    public ArrayList<String> getLore(int gemNumber) {
        return (ArrayList<String>) file.getStringList("Gem" + GemManager.lookUpName(gemNumber) + "Lore");
    }

}
