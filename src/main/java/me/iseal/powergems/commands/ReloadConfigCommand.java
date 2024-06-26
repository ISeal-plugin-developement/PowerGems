package me.iseal.powergems.commands;

import me.iseal.powergems.Main;
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
        Main.config.forceReload();
        Main.gemActive.forceReload();
        Main.cd.forceReload();
        sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
        return false;
    }
}
