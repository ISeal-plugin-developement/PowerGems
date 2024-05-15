package me.iseal.powergems.misc;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public abstract class Gem {

    protected Logger l = Bukkit.getLogger();
    protected Player plr;
    protected Class<?> caller = null;
    protected SingletonManager sm = Main.getSingletonManager();
    protected GemManager gm = sm.gemManager;
    protected CooldownManager cm = sm.cooldownManager;
    protected ConfigManager configManager = sm.configManager;
    protected int level;

    public void call(Action action, Player plr, ItemStack item) {
        if (action.equals(Action.PHYSICAL))
            return;
        level = gm.getLevel(item);
        this.plr = plr;
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
        if (action.equals("left")) {
            return cm.isLeftClickOnCooldown(plr, caller);
        } else if (action.equals("right")) {
            return cm.isRightClickOnCooldown(plr, caller);
        } else if (action.equals("shift")) {
            return cm.isShiftClickOnCooldown(plr, caller);
        }
        return false;
    }

    protected abstract void rightClick(Player plr);

    protected abstract void leftClick(Player plr);

    protected abstract void shiftClick(Player plr);
}
