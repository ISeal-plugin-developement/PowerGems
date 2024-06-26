package me.iseal.powergems.commands;

import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GiveGemCommand implements CommandExecutor, TabCompleter {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final ArrayList<String> possibleTabCompletions = new ArrayList<>();

    {
        sm.gemManager.getAllGems().forEach(((integer, itemStack) -> {
            possibleTabCompletions.add(sm.gemManager.lookUpName(integer));
        }));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can execute this command!");
            return true;
        }
        Player plr = (Player) commandSender;
        if (!plr.hasPermission(command.getPermission())) {
            plr.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }
        if (args.length < 1) {
            plr.getInventory().addItem(sm.gemManager.createGem());
            return true;
        } else {
            String gemNumString = args[0];
            if (isNumber(gemNumString)) {
                if (args.length >= 2) {
                    String gemLvlString = args[1];
                    if (isNumber(gemLvlString)) {
                        plr.getInventory().addItem(
                                sm.gemManager.createGem(Integer.valueOf(gemNumString), Integer.valueOf(gemLvlString)));
                        return true;
                    }
                }
                plr.getInventory().addItem(sm.gemManager.createGem(Integer.valueOf(gemNumString)));
                return true;
            } else {
                if (sm.gemManager.lookUpID(gemNumString) != -1) {
                    if (args.length >= 2) {
                        String gemLvlString = args[1];
                        if (isNumber(gemLvlString)) {
                            plr.getInventory().addItem(sm.gemManager.createGem(sm.gemManager.lookUpID(gemNumString),
                                    Integer.valueOf(gemLvlString)));
                            return true;
                        }
                    }
                    plr.getInventory().addItem(sm.gemManager.createGem(sm.gemManager.lookUpID(gemNumString)));
                    return true;
                }
                plr.sendMessage(ChatColor.DARK_RED + "Invalid gem name / ID.");
                return true;
            }
        }
    }

    private boolean isNumber(String num) {
        try {
            Integer.valueOf(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        ArrayList<String> toReturn = new ArrayList<>();
        if (args.length == 1) {
            for (String str : possibleTabCompletions) {
                if (str.toLowerCase().contains(args[0].toLowerCase())) {
                    toReturn.add(str);
                }
            }
        }
        return toReturn;
    }
}
