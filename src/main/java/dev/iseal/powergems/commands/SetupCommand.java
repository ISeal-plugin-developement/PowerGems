package dev.iseal.powergems.commands;

import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission(command.getPermission())) return true;
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(I18N.translate("NOT_PLAYER"));
            return true;
        }
        return true;
    }
}

