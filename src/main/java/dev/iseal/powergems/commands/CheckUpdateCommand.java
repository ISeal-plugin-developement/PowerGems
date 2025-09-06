package dev.iseal.powergems.commands;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.powergems.managers.UpdaterManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CheckUpdateCommand implements CommandExecutor {

    private final UpdaterManager um = SingletonManager.getInstance().updaterManager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (sender.hasPermission(command.getPermission())) {
            sender.sendMessage(I18N.translate("STARTING_UPDATE_CHECK"));
            um.startUpdate(sender);
            return true;
        }
        sender.sendMessage(I18N.translate("NO_PERMISSION"));
        return true;
    }
}
