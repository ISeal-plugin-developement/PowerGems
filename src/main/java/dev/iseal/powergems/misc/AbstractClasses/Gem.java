package dev.iseal.powergems.misc.AbstractClasses;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.Addons.WorldGuard.WorldGuardAddonManager;
import dev.iseal.powergems.managers.Configuration.GemParticleConfigManager;
import dev.iseal.sealLib.Systems.I18N.I18N;

public abstract class Gem {

    protected Logger l = Bukkit.getLogger();
    protected Player plr;
    protected Class<?> caller = this.getClass();
    protected SingletonManager sm = SingletonManager.getInstance();
    protected GemManager gm = sm.gemManager;
    protected CooldownManager cm = sm.cooldownManager;
    protected GemParticleConfigManager gpcm = sm.configManager.getRegisteredConfigInstance(GemParticleConfigManager.class);
    protected int level;
    protected Particle particle;
    protected String name;

    public Gem(String name) {
        this.name = name;
    }

    public void call(Action action, Player plr, ItemStack item) {
        if (action.equals(Action.PHYSICAL))
            return;
        level = gm.getLevel(item);
        this.plr = plr;
        if (PowerGems.isWorldGuardEnabled && !WorldGuardAddonManager.getInstance().isGemUsageAllowedInRegion(plr)) {
            plr.sendMessage(I18N.getTranslation("CANNOT_USE_GEMS_IN_REGION"));
            return;
        }
        if (plr.isSneaking()) {
            if (checkIfCooldown("shift", plr)) {
                return;
            }
            shiftClick(plr);
            cm.setShiftClickCooldown(plr, cm.getFullCooldown(level, caller.getSimpleName(), "Shift"), caller);
        } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (checkIfCooldown("left", plr)) {
                return;
            }
            leftClick(plr);
            cm.setLeftClickCooldown(plr, cm.getFullCooldown(level, caller.getSimpleName(), "Left"), caller);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (checkIfCooldown("right", plr)) {
                return;
            }
            rightClick(plr);
            cm.setRightClickCooldown(plr, cm.getFullCooldown(level, caller.getSimpleName(), "Right"), caller);
        }
    }

    protected boolean checkIfCooldown(String action, Player plr) {
        return switch (action) {
            case "left" -> cm.isLeftClickOnCooldown(plr, caller);
            case "right" -> cm.isRightClickOnCooldown(plr, caller);
            case "shift" -> cm.isShiftClickOnCooldown(plr, caller);
            default -> false;
        };
    }

    protected abstract void rightClick(Player plr);

    protected abstract void leftClick(Player plr);

    protected abstract void shiftClick(Player plr);
    
    public Particle particle() {
        if (particle == null) {
            particle = gpcm.getParticle(GemManager.lookUpID(name));
        }
        return particle;
    }

    public String getName() {
        return name;
    }

    public ItemStack gemInfo(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        
        // Add basic gem info
        lore.add(ChatColor.GREEN + "Level: " + level);
        lore.add(ChatColor.GREEN + "Type: " + name + " Gem");
        
        // Add cooldown info using CooldownManager
        lore.add(ChatColor.YELLOW + "Cooldowns:");
        lore.add(ChatColor.WHITE + "Left Click: " + cm.getFullCooldown(level, caller.getSimpleName(), "Left") + "s");
        lore.add(ChatColor.WHITE + "Right Click: " + cm.getFullCooldown(level, caller.getSimpleName(), "Right") + "s"); 
        lore.add(ChatColor.WHITE + "Shift Click: " + cm.getFullCooldown(level, caller.getSimpleName(), "Shift") + "s");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}