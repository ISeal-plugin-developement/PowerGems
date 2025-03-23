package dev.iseal.powergems.tasks;

import dev.iseal.powergems.managers.Configuration.GemPermanentEffectLevelConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class PermanentEffectsGiverTask extends BukkitRunnable {

    private final GemManager gemManager = GemManager.getInstance();
    private final GemPermanentEffectLevelConfigManager gpelcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GemPermanentEffectLevelConfigManager.class);
    private final GeneralConfigManager gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(
                plr -> gemManager.getPlayerGems(plr).forEach(
                        gem -> {
                            Gem instance = gemManager.getGemInstance(gem, plr);
                            if (instance.getLevel() <= gcm.unlockNewAbilitiesOnLevelX())
                                return;
                            if (instance.getEffect() == null)
                                return;
                            PotionEffect effect = new PotionEffect(
                                    instance.getEffect(),
                                    100,
                                    gpelcm.getLevel(GemManager.lookUpID(instance.getName()))
                            );
                            plr.addPotionEffect(effect);
                        }
                )
        );
    }
}
