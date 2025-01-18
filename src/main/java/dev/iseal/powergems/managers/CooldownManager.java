package dev.iseal.powergems.managers;

import dev.iseal.powergems.managers.Configuration.CooldownConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.misc.Interfaces.Dumpable;
import dev.iseal.powergems.misc.WrapperObjects.CooldownObject;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CooldownManager implements Dumpable {

    private final ConfigManager cm = SingletonManager.getInstance().configManager;
    private final GeneralConfigManager gcm = cm.getRegisteredConfigInstance(GeneralConfigManager.class);
    private final CooldownConfigManager ccm = cm.getRegisteredConfigInstance(CooldownConfigManager.class);

    private final BlockingQueue<CooldownObject> rightClickCooldowns = new LinkedBlockingQueue<>();
    private final BlockingQueue<CooldownObject> leftClickCooldowns = new LinkedBlockingQueue<>();
    private final BlockingQueue<CooldownObject> shiftClickCooldowns = new LinkedBlockingQueue<>();

    private static CooldownManager instance;
    public static CooldownManager getInstance() {
        if (instance == null) {
            instance = new CooldownManager();
        }
        return instance;
    }

    public void setRightClickCooldown(Player player, long time, Class<?> fromClass) {
        if (gcm.isDragonEggHalfCooldown() && player.getInventory().contains(Material.DRAGON_EGG)) {
            rightClickCooldowns.add(new CooldownObject(player, fromClass, (time * 500) + System.currentTimeMillis()));
            return;
        }
        rightClickCooldowns.add(new CooldownObject(player, fromClass, (time * 1000) + System.currentTimeMillis()));
    }

    public void setLeftClickCooldown(Player player, long time, Class<?> fromClass) {
        if (gcm.isDragonEggHalfCooldown() && player.getInventory().contains(Material.DRAGON_EGG)) {
            leftClickCooldowns.add(new CooldownObject(player, fromClass, (time * 500) + System.currentTimeMillis()));
            return;
        }
        leftClickCooldowns.add(new CooldownObject(player, fromClass, (time * 1000) + System.currentTimeMillis()));
    }

    public void setShiftClickCooldown(Player player, long time, Class<?> caller) {
        if (gcm.isDragonEggHalfCooldown() && player.getInventory().contains(Material.DRAGON_EGG)) {
            shiftClickCooldowns.add(new CooldownObject(player, caller, (time * 500) + System.currentTimeMillis()));
            return;
        }
        shiftClickCooldowns.add(new CooldownObject(player, caller, (time * 1000) + System.currentTimeMillis()));
    }

    public boolean isRightClickOnCooldown(Player player, Class<?> fromClass) {
        return getRightClickCooldown(player, fromClass) > System.currentTimeMillis();
    }

    public boolean isLeftClickOnCooldown(Player player, Class<?> fromClass) {
        return getLeftClickCooldown(player, fromClass) > System.currentTimeMillis();
    }

    public boolean isShiftClickOnCooldown(Player plr, Class<?> caller) {
        return getShiftClickCooldown(plr, caller) > System.currentTimeMillis();
    }

    public long getRightClickCooldown(Player player, Class<?> fromClass) {
        for (CooldownObject co : rightClickCooldowns) {
            if (co.getPlayer() == player) {
                if (co.getFromClass() == fromClass) {
                    if (co.getTime() < System.currentTimeMillis()) {
                        leftClickCooldowns.remove(co);
                        continue;
                    }
                    return co.getTime();
                }
            }
        }
        return 0;
    }

    public long getLeftClickCooldown(Player player, Class<?> fromClass) {
        for (CooldownObject co : leftClickCooldowns) {
            if (co.getPlayer() == player) {
                if (co.getFromClass() == fromClass) {
                    if (co.getTime() < System.currentTimeMillis()) {
                        leftClickCooldowns.remove(co);
                        continue;
                    }
                    return co.getTime();
                }
            }
        }
        return 0;
    }

    public long getShiftClickCooldown(Player plr, Class<?> caller) {
        for (CooldownObject co : shiftClickCooldowns) {
            if (co.getPlayer() == plr) {
                if (co.getFromClass() == caller) {
                    if (co.getTime() < System.currentTimeMillis()) {
                        shiftClickCooldowns.remove(co);
                        continue;
                    }
                    return co.getTime();
                }
            }
        }
        return 0;
    }

    public String getFormattedTimer(Player plr, Class<?> caller, String action) {
        long cooldownMillis = 0;
        if (action.equals("left")) {
            if (getLeftClickCooldown(plr, caller) < System.currentTimeMillis())
                return I18N.translate("READY");
            cooldownMillis = getLeftClickCooldown(plr, caller) - System.currentTimeMillis();
        } else if (action.equals("right")) {
            if (getRightClickCooldown(plr, caller) < System.currentTimeMillis())
                return I18N.translate("READY");
            cooldownMillis = getRightClickCooldown(plr, caller) - System.currentTimeMillis();
        } else if (action.equals("shift")) {
            if (getShiftClickCooldown(plr, caller) < System.currentTimeMillis())
                return I18N.translate("READY");
            cooldownMillis = getShiftClickCooldown(plr, caller) - System.currentTimeMillis();
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(cooldownMillis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(cooldownMillis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(cooldownMillis);
        String endTime;
        if (seconds < 10) {
            endTime = String.format("0%d:0%d", minutes, seconds);
        } else {
            endTime = String.format("0%d:%d", minutes, seconds);
        }
        return ChatColor.DARK_RED + endTime;
    }

    public long getFullCooldown(int level, String name, String ability) {
        return ccm.getStartingCooldown(name, ability) - gcm.getGemCooldownBoost() * level;
    }

    public void cancelCooldowns() {
        rightClickCooldowns.clear();
        leftClickCooldowns.clear();
    }

    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("rightClickCooldowns", rightClickCooldowns);
        map.put("leftClickCooldowns", leftClickCooldowns);
        map.put("shiftClickCooldowns", shiftClickCooldowns);
        return map;
    }
}
