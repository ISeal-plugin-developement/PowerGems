package me.iseal.powergems.commands;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class giveAllGemCommand implements CommandExecutor {

    SingletonManager sm = Main.getSingletonManager();
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)){commandSender.sendMessage("Only players can execute this command!");return true;}
        Player plr = (Player) commandSender;
        if (!plr.hasPermission(command.getPermission())){plr.sendMessage(ChatColor.DARK_RED+"You do not have permission to execute this command.");return true;}
            if (args.length < 1) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.getInventory().addItem(sm.gemManager.createGem());
                }
                return true;
            } else {
                String gemNumString = args[0];
                if (isNumber(gemNumString)) {
                    int gemInt = Integer.valueOf(gemNumString);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.getInventory().addItem(sm.gemManager.createGem(gemInt));
                    }
                }
                return true;
            }
        }

    private boolean isNumber(String num){
        try {
            Integer.valueOf(num);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

}

