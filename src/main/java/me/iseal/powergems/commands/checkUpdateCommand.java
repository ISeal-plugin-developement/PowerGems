package me.iseal.powergems.commands;

import me.iseal.powergems.Main;
import me.iseal.powergems.managers.UpdaterManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class checkUpdateCommand implements CommandExecutor {

    UpdaterManager um = Main.getSingletonManager().updaterManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission(command.getPermission())){
            sender.sendMessage("Starting check");
            um.startUpdate(sender);
            return true;
        }
        sender.sendMessage("You don't have permission to run this command");
        return true;
    }
}
