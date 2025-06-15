package dev.iseal.powergems.tasks;

import dev.iseal.powergems.managers.Configuration.GemPermanentEffectConfigManager;
import dev.iseal.powergems.managers.Configuration.GemPermanentEffectLevelConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PermanentEffectsGiverTask extends BukkitRunnable {

    private final GemManager gemManager = GemManager.getInstance();
    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemPermanentEffectLevelConfigManager gpelcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GemPermanentEffectLevelConfigManager.class);
    private final GemPermanentEffectConfigManager gpecm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GemPermanentEffectConfigManager.class);
    private final GeneralConfigManager gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(
                plr -> gemManager.getPlayerGems(plr).forEach(
                        gem -> {
                            Gem instance = gemManager.getGemInstance(gem, plr);
                            int level = gemManager.getLevel(gem);
                            if (level <= gcm.unlockNewAbilitiesOnLevelX())
                                return;
                            if (instance.getDefaultEffectType() == null)
                                return;

                           utils.addPreciseEffectIgnoringDuration(
                                   plr,
                                   gpecm.getType(instance.getName()),
                                   100,
                                   gpelcm.getLevel(instance.getName())
                           );
                        }
                )
        );
    }
}
