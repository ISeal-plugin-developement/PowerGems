package dev.iseal.powergems.tasks;

import dev.iseal.powergems.managers.Configuration.GeneralConfigManager;
import dev.iseal.powergems.misc.WrapperObjects.SchedulerWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import org.bukkit.scheduler.BukkitRunnable;

public class AddCooldownToToolBar extends BukkitRunnable {

    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final CooldownManager cm = SingletonManager.getInstance().cooldownManager;
    private final TempDataManager tdm = SingletonManager.getInstance().tempDataManager;
    private final boolean unlockShiftAbilityOnLevelX = SingletonManager.getInstance().configManager
            .getRegisteredConfigInstance(GeneralConfigManager.class).unlockShiftAbilityOnLevelX();
    private final int unlockNewAbilitiesOnLevelX = SingletonManager.getInstance().configManager
            .getRegisteredConfigInstance(GeneralConfigManager.class).unlockNewAbilitiesOnLevelX();
    private final SchedulerWrapper schedulerWrapper = SingletonManager.getInstance().schedulerWrapper;

    private void sendCooldownMessage(Player plr, ItemStack item) {
        if (item != null && gm.isGem(item)) {
            Class<?> clazz = gm.getGemClass(item, plr);
            int level = gm.getLevel(item);

            Component leftTimer = cm.getFormattedTimer(plr, clazz, "left");
            Component rightTimer = cm.getFormattedTimer(plr, clazz, "right");

            Component message;

            if(!unlockShiftAbilityOnLevelX || level >= unlockNewAbilitiesOnLevelX) {
                Component shiftTimer = cm.getFormattedTimer(plr, clazz, "shift");
                message = Component.text()
                        .append(leftTimer)
                        .append(Component.text(" | ").color(NamedTextColor.WHITE))
                        .append(shiftTimer)
                        .append(Component.text(" | ").color(NamedTextColor.WHITE))
                        .append(rightTimer)
                        .build();
            } else {
                message = Component.text()
                        .append(leftTimer)
                        .append(Component.text(" | ").color(NamedTextColor.WHITE))
                        .append(rightTimer)
                        .build();
            }

            plr.sendActionBar(message);
        }
    }

    @Override
    public void run() {
        Bukkit.getServer().getOnlinePlayers().forEach(player ->
            schedulerWrapper.scheduleTaskForEntity(player, () -> {
                if (tdm.chargingFireball.containsKey(player)) {
                    return;
                }
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                ItemStack offHand = player.getInventory().getItemInOffHand();

                sendCooldownMessage(player, mainHand);
                sendCooldownMessage(player, offHand);
            })
        );
    }
}
