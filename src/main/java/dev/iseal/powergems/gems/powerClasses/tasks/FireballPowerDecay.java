package dev.iseal.powergems.gems.powerClasses.tasks;

import dev.iseal.powergems.managers.SingletonManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class FireballPowerDecay implements Runnable {

    private final TempDataManager tdm = SingletonManager.getInstance().tempDataManager;
    private final GeneralConfigManager gdm = SingletonManager.getInstance().configManager
            .getRegisteredConfigInstance(GeneralConfigManager.class);
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;

    public Player plr = null;
    public int currentPower = 0;
    public int level = 1;

//    private final Object taskHandle = null;
    private boolean cancelled = false;

    public void cancel() {
        cancelled = true;
//        if (taskHandle != null) {
//        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void run() {
        if (cancelled || !plr.isValid() || !plr.isOnline()) {
            cancel();
            return;
        }

        currentPower = Math.max(0, Math.min(100, currentPower + (plr.isSneaking() ? 9 : -1)));

        if (currentPower <= 0) {
            tdm.chargingFireball.remove(plr);
            plr.sendActionBar(Component.text(I18N.translate("FIREBALL_FAIL_LAUNCH"), NamedTextColor.RED));
            cancel();
            return;
        }

        if (currentPower >= 100) {
            spawnFireball();
            tdm.chargingFireball.remove(plr);
            plr.sendActionBar(Component.text(I18N.translate("FIREBALL_LAUNCHED"), NamedTextColor.GREEN));
            cancel();
            return;
        }

        int bars = currentPower / 10;
        Component powerBar = Component.text()
                .append(Component.text("| ".repeat(bars), NamedTextColor.GREEN))
                .append(Component.text("| ".repeat(10 - bars), NamedTextColor.GRAY))
                .build();
        plr.sendActionBar(powerBar);
    }

    private void spawnFireball() {
        Vector direction = plr.getEyeLocation().getDirection();
        Fireball fireball = plr.launchProjectile(Fireball.class, direction.multiply(2));

        fireball.setYield(5 + level);
        fireball.setVisibleByDefault(false);
        fireball.setIsIncendiary(gdm.isExplosionDamageAllowed());

        PersistentDataContainer pdc = fireball.getPersistentDataContainer();
        pdc.set(nkm.getKey("is_gem_explosion"), PersistentDataType.BOOLEAN, true);
        pdc.set(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN, true);
    }
}
