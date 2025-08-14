package dev.iseal.powergems.misc.AbstractClasses;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.Addons.CombatLogX.CombatLogXAddonManager;
import dev.iseal.powergems.managers.Addons.WorldGuard.WorldGuardAddonManager;
import dev.iseal.powergems.managers.Configuration.GemParticleConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
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

        this.plr = plr;
        int level = gm.getLevel(item);
        if (PowerGems.isWorldGuardEnabled && !WorldGuardAddonManager.getInstance().isGemUsageAllowedInRegion(plr)) {
            plr.sendMessage(I18N.translate("CANNOT_USE_GEMS_IN_REGION"));
            return;
        }
        // check if sneaking, or else pass to other checks
        if (plr.isSneaking()) {
            // if sneaking, check if shift ability needs to unlock
            if (
                    gcm.unlockShiftAbilityOnLevelX()
                    && level < gcm.unlockNewAbilitiesOnLevelX()) {
                return;
            }
            // if shift ability is unlocked or not needed, check if shift ability is on cooldown
            if (checkIfCooldown("shift", plr)) {
                return;
            }

            // add this usage to gemManager's list
            gm.addGemUsage(caller.getSimpleName(), "Shift");

            // finally, call the shift ability
            shiftClick(plr, level);
            // if combatlogx is enabled, set in fight
            if (PowerGems.isEnabled("CombatLogX") && gcm.isCombatLogXEnabled())
                CombatLogXAddonManager.getInstance().setInFightAttacker(plr);

            cm.setShiftClickCooldown(plr, cm.getFullCooldown(level, caller.getSimpleName(), "Shift"), caller);
        } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (checkIfCooldown("left", plr)) {
                return;
            }
            // add this usage to gemManager's list
            gm.addGemUsage(caller.getSimpleName(), "Left");

            leftClick(plr, level);
            if (PowerGems.isEnabled("CombatLogX") && gcm.isCombatLogXEnabled())
                CombatLogXAddonManager.getInstance().setInFightAttacker(plr);
            cm.setLeftClickCooldown(plr, cm.getFullCooldown(level, caller.getSimpleName(), "Left"), caller);
        } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (checkIfCooldown("right", plr)) {
                return;
            }
            // add this usage to gemManager's list
            gm.addGemUsage(caller.getSimpleName(), "Right");

            rightClick(plr, level);
            if (PowerGems.isEnabled("CombatLogX") && gcm.isCombatLogXEnabled())
                CombatLogXAddonManager.getInstance().setInFightAttacker(plr);
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

    protected abstract void rightClick(Player plr, int level);

    protected abstract void leftClick(Player plr, int level);

    protected abstract void shiftClick(Player plr, int level);

    public abstract ArrayList<String> getDefaultLore();

    public abstract PotionEffectType getDefaultEffectType();

    public abstract int getDefaultEffectLevel();

    public abstract Particle getDefaultParticle();

    /**
     * Get the block data for the particle effect.
     * This is used for particles like BLOCK_CRACK, BLOCK_DUST, and FALLING_DUST.
     * @return BlockData for the particle effect, or null if not applicable.
     */
    public abstract BlockData getParticleBlockData();


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
