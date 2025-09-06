package dev.iseal.powergems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class GetAllGemsCommand implements CommandExecutor {

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

        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(I18N.translate("INVENTORY_FULL"));
                return true;
            }
            player.getInventory().addItem(sm.gemManager.createGem(i));
        }
        
        player.sendMessage(I18N.translate("ALL_GEMS_GIVEN"));
        return true;
    }
}