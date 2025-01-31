package dev.iseal.powergems.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dev.iseal.powergems.managers.CooldownManager;
import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.TempDataManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class AddCooldownToToolBar extends BukkitRunnable {

    private final GemManager gm = SingletonManager.getInstance().gemManager;
    private final CooldownManager cm = SingletonManager.getInstance().cooldownManager;
    private final TempDataManager tdm = SingletonManager.getInstance().tempDataManager;


    private void sendCooldownMessage(Player plr, ItemStack item) {
        if (item != null && gm.isGem(item)) {
            Class<?> clazz = gm.getGemClass(item, plr);
            plr.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(cm.getFormattedTimer(plr, clazz, "left") + ChatColor.GREEN + " | "
                            + cm.getFormattedTimer(plr, clazz, "right") + ChatColor.GREEN + " | "
                            + cm.getFormattedTimer(plr, clazz, "shift")));
        }
    }

    @Override
    public void run() {
        Bukkit.getServer().getOnlinePlayers().forEach(plr -> {
            if (tdm.chargingFireball.containsKey(plr)) {
                return;
            }
            ItemStack mainHand = plr.getInventory().getItemInMainHand();
            ItemStack offHand = plr.getInventory().getItemInOffHand();

            sendCooldownMessage(plr, mainHand);
            sendCooldownMessage(plr, offHand);
        });
    }
}
