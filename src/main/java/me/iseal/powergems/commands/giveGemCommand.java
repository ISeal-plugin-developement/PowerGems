package me.iseal.powergems.commands;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class giveGemCommand implements CommandExecutor {

    private final SingletonManager sm = Main.getSingletonManager();
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)){commandSender.sendMessage("Only players can execute this command!");return true;}
        Player plr = (Player) commandSender;
        if (!plr.hasPermission(command.getPermission())){plr.sendMessage(ChatColor.DARK_RED+"You do not have permission to execute this command.");return true;}
        if (args.length < 1) {
            plr.getInventory().addItem(sm.gemManager.createGem());
            return true;
        } else {
            String gemNumString = args[0];
            if (isNumber(gemNumString)){
                if (args.length >= 2) {
                    String gemLvlString = args[1];
                    if (isNumber(gemLvlString)){
                        plr.getInventory().addItem(sm.gemManager.createGem(Integer.valueOf(gemNumString), Integer.valueOf(gemLvlString)));
                        return true;
                    }
                }
                plr.getInventory().addItem(sm.gemManager.createGem(Integer.valueOf(gemNumString)));
                return true;
            } else {
                plr.getInventory().addItem(sm.gemManager.createGem());
                return true;
            }
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
