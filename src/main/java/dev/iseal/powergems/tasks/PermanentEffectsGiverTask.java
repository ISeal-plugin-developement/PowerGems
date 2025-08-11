package dev.iseal.powergems.tasks;

import dev.iseal.powergems.managers.Configuration.GemPermanentEffectConfigManager;
import dev.iseal.powergems.managers.Configuration.GemPermanentEffectLevelConfigManager;
import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.misc.AbstractClasses.Gem;
import dev.iseal.powergems.misc.Utils;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PermanentEffectsGiverTask extends BukkitRunnable {

    private final GemManager gemManager = GemManager.getInstance();
    private final Utils utils = SingletonManager.getInstance().utils;
    private final GemPermanentEffectLevelConfigManager gpelcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GemPermanentEffectLevelConfigManager.class);
    private final GemPermanentEffectConfigManager gpecm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GemPermanentEffectConfigManager.class);
    private final GeneralConfigManager gcm = SingletonManager.getInstance().configManager.getRegisteredConfigInstance(GeneralConfigManager.class);
    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player ->
                schedulerWrapper.scheduleTaskForEntity(player, () -> {
                    gemManager.getPlayerGems(player).forEach(
                            gem -> {
                                Gem instance = gemManager.getGemInstance(gem, player);
                                int level = gemManager.getLevel(gem);
                                if (level <= gcm.unlockNewAbilitiesOnLevelX())
                                    return;
                                if (instance.getDefaultEffectType() == null)
                                    return;
                                PotionEffectType effectType = gpecm.getType(instance.getName());
                                if (effectType == null)
                                    return;

                                utils.addPreciseEffectIgnoringDuration(
                                        player,
                                        effectType,
                                        100,
                                        gpelcm.getLevel(instance.getName())
                                );
                            }
                    );
                })
        );
    }
}
