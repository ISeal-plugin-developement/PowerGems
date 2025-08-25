package dev.iseal.powergems.commands;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveAllPlayersGemCommand implements CommandExecutor {

    private final SingletonManager sm = SingletonManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(I18N.translate("NOT_PLAYER"));
            return true;
        }

        if (!player.hasPermission(command.getPermission())) {
            player.sendMessage(I18N.translate("NO_PERMISSION"));
            return true;
        }

        if (args.length < 1) {
            Bukkit.getOnlinePlayers().forEach(p -> 
                p.getInventory().addItem(sm.gemManager.createGem())
            );
            return true;
        }

        String gemNumString = args[0];
        if (isNumber(gemNumString)) {
            int gemInt = Integer.parseInt(gemNumString);
            Bukkit.getOnlinePlayers().forEach(p -> 
                p.getInventory().addItem(sm.gemManager.createGem(gemInt))
            );
        }
        return true;
    }

    private boolean isNumber(String num) {
        try {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
