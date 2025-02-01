package dev.iseal.powergems.misc.AbstractClasses;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Addons.WorldGuard.WorldGuardAddonManager;
import dev.iseal.powergems.managers.Configuration.GemParticleConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

public abstract class Gem {

    protected Logger l = Bukkit.getLogger();
    protected Player plr;
    protected Class<?> caller = this.getClass();
    protected SingletonManager sm = SingletonManager.getInstance();
    protected GemManager gm = sm.gemManager;
    protected CooldownManager cm = sm.cooldownManager;
    protected GemParticleConfigManager gpcm = sm.configManager.getRegisteredConfigInstance(GemParticleConfigManager.class);
    protected GeneralConfigManager gcm = sm.configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
    protected int level;
    protected Particle particle;
    protected String name;

    public Gem(String name) {
        this.name = name;
    }

    public void call(Action action, Player plr, ItemStack item) {
        if (action.equals(Action.PHYSICAL))
            return;

        if (gcm.doAttemptFixOldGems()){
            gm.attemptFixGem(item);
        }

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
}
