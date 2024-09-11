package dev.iseal.powergems.commands;

import dev.iseal.powergems.managers.Metrics.ConnectionManager;
import dev.iseal.powergems.managers.SingletonManager;
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

public class DebugCommand implements CommandExecutor, TabCompleter {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final ArrayList<String> possibleTabCompletions = new ArrayList<>();

    {
        possibleTabCompletions.add("cancelCooldowns");
        possibleTabCompletions.add("resetConfig");
        possibleTabCompletions.add("forceMetricsSave");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "You need to be a player to execute this command!");
            return true;
        }
        Player plr = (Player) sender;
        if (!plr.hasPermission(command.getPermission())) {
            plr.sendMessage(ChatColor.DARK_RED + "You do not have permission to execute this command.");
            return true;
        }
        if (args.length < 1) {
            plr.sendMessage(ChatColor.DARK_RED + "You need to specify a subcommand.");
            return true;
        }

        switch (args[0]) {
            case "cancelCooldowns":
                sm.cooldownManager.cancelCooldowns();
                break;
            case "resetConfig":
                sm.configManager.resetConfig();
                break;
            case "forceMetricsSave":
                sm.metricsManager.exitAndSendInfo();
                break;
            case "invalidateToken":
                ConnectionManager.getInstance().invalidateToken();
                break;
            default:
                plr.sendMessage(ChatColor.DARK_RED + "Invalid subcommand.");
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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
