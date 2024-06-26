package me.iseal.powergems.commands;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.SingletonManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class debugCommand implements CommandExecutor {

    private final SingletonManager sm = Main.getSingletonManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + SingletonManager.configManager.getGeneralConfigManager().getPluginPrefix() + "You need to be a player to execute this command!");
            return true;
        }
        Player plr = (Player) sender;
        if (!plr.hasPermission(command.getPermission())) {
            plr.sendMessage("You do not have permission to execute this command.");
            return true;
        }
        if (args.length < 1) {
            plr.sendMessage("You need to specify a subcommand.");
            return true;
        }

        switch (args[0]) {
            case "cancelCooldowns":
                sm.cooldownManager.cancelCooldowns();
                break;
            case "resetConfig":
                sm.configManager.resetConfig();
                break;
            default:
                plr.sendMessage("Invalid subcommand.");
                break;
        }
        return true;
    }
}
