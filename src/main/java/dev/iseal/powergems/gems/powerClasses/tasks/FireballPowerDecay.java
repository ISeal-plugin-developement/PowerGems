package dev.iseal.powergems.gems.powerClasses.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class FireballPowerDecay extends BukkitRunnable {

    private final TempDataManager tdm = SingletonManager.getInstance().tempDataManager;
    private final GeneralConfigManager gdm = SingletonManager.getInstance().configManager
            .getRegisteredConfigInstance(GeneralConfigManager.class);
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    public Player plr = null;
    public int currentPower = 0;
    public int level = 1;

    @Override
    public void run() {
        if (!plr.isValid() || !plr.isOnline()) {
            this.cancel();
            return;
        }

        currentPower = Math.max(0, Math.min(100, currentPower + (plr.isSneaking() ? 9 : -1)));

        if (currentPower <= 0) {
            tdm.chargingFireball.remove(plr);
            plr.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(I18N.translate("FIREBALL_FAIL_LAUNCH"))
            );
            this.cancel();
            return;
        }

        if (currentPower >= 100) {
            spawnFireball();
            tdm.chargingFireball.remove(plr);
            plr.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(I18N.translate("FIREBALL_LAUNCHED"))
            );
            this.cancel();
            return;
        }

        int bars = currentPower / 10;
        String powerBar = ChatColor.GREEN + "| ".repeat(bars) + ChatColor.GRAY + "| ".repeat(10 - bars);
        plr.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(powerBar));
    }

    private void spawnFireball() {
        Vector direction = plr.getEyeLocation().getDirection();
        Fireball fireball = plr.launchProjectile(Fireball.class, direction.multiply(2));

        fireball.setYield(5 + level);
        fireball.setVisualFire(false);
        fireball.setIsIncendiary(gdm.isExplosionDamageAllowed());

        PersistentDataContainer pdc = fireball.getPersistentDataContainer();
        pdc.set(nkm.getKey("is_gem_explosion"), PersistentDataType.BOOLEAN, true);
        pdc.set(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN, true);
    }
}
