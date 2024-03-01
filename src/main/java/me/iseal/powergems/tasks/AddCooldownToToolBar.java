package me.iseal.powergems.tasks;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.CooldownManager;
import me.iseal.powergems.managers.GemManager;
import me.iseal.powergems.managers.TempDataManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class AddCooldownToToolBar extends BukkitRunnable {

    private final GemManager gm = Main.getSingletonManager().gemManager;
    private final CooldownManager cm = Main.getSingletonManager().cooldownManager;
    private final TempDataManager tdm = Main.getSingletonManager().tempDataManager;

    @Override
    public void run() {
        Bukkit.getServer().getOnlinePlayers().forEach(plr -> {
            if (tdm.chargingFireball.containsKey(plr)){
                return;
            }
            if (gm.isGem(plr.getInventory().getItemInMainHand())) {
                Class<?> clazz = gm.getGemClass(plr.getInventory().getItemInMainHand());
                plr.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(cm.getFormattedTimer(plr, clazz, "right")+ChatColor.GREEN+" | "+cm.getFormattedTimer(plr, clazz, "left")+ChatColor.GREEN+" | "+cm.getFormattedTimer(plr,clazz,"shift")));
            } else if (gm.isGem(plr.getInventory().getItemInOffHand())) {
                Class<?> clazz = gm.getGemClass(plr.getInventory().getItemInOffHand());
                plr.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(cm.getFormattedTimer(plr, clazz, "right")+ChatColor.GREEN+" | "+cm.getFormattedTimer(plr, clazz, "left")+ChatColor.GREEN+" | "+cm.getFormattedTimer(plr,clazz,"shift")));

            }
        });
    }
}
