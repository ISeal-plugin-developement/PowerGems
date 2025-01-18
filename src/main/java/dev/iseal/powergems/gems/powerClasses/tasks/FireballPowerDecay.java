package dev.iseal.powergems.gems.powerClasses.tasks;

import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.NamespacedKeyManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireballPowerDecay extends BukkitRunnable {

    private final TempDataManager tdm = SingletonManager.getInstance().tempDataManager;
    private final GeneralConfigManager gdm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
    private final NamespacedKeyManager nkm = SingletonManager.getInstance().namespacedKeyManager;
    public Player plr = null;
    public int currentPower = 0;
    public int level = 1;

    @Override
    public void run() {
        currentPower -= 1;
        if (plr.isSneaking()) {
            currentPower += 10;
        }
        if (currentPower <= 0) {
            tdm.chargingFireball.remove(plr);
            plr.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(I18N.getTranslation("FIREBALL_FAIL_LAUNCH")));
            this.cancel();
            return;
        }
        if (currentPower >= 100) {
            spawnFireball();
            tdm.chargingFireball.remove(plr);
            plr.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(I18N.getTranslation("FIREBALL_LAUNCHED")));
            this.cancel();
            return;
        }
        StringBuilder toSay = new StringBuilder(ChatColor.GREEN + "");
        int nOfBars = currentPower / 10;
        toSay.append("| ".repeat(Math.max(0, nOfBars)));
        toSay.append(ChatColor.GRAY);
        toSay.append("| ".repeat(Math.max(0, 10 - nOfBars)));
        plr.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(toSay.toString()));
    }

    private void spawnFireball() {
        Vector direction = plr.getEyeLocation().getDirection();
        Fireball fireball = plr.launchProjectile(Fireball.class);
        fireball.setVelocity(direction.multiply(2));
        fireball.setYield(5 + level);
        fireball.setVisualFire(false);
        fireball.setIsIncendiary(gdm.isExplosionDamageAllowed());
        fireball.getPersistentDataContainer().set(nkm.getKey("is_gem_explosion"), PersistentDataType.BOOLEAN, true);
        fireball.getPersistentDataContainer().set(nkm.getKey("is_gem_projectile"), PersistentDataType.BOOLEAN, true);
    }

}
