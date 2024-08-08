package dev.iseal.powergems.commands;

import dev.iseal.powergems.managers.SingletonManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadConfigCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission(command.getPermission())) {
            sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command!");
            return true;
        }
        sender.sendMessage(ChatColor.GREEN + "Reloading config...");
        SingletonManager.getInstance().configManager.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
        return false;
    }
}
