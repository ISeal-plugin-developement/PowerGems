package dev.iseal.powergems.commands;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.I18N.I18N;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetAllGemsCommand implements CommandExecutor {

    private final SingletonManager sm = SingletonManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(I18N.getTranslation("NOT_PLAYER"));
            return true;
        }
        Player plr = (Player) commandSender;
        if (!plr.hasPermission(command.getPermission())) {
            plr.sendMessage(I18N.getTranslation("NO_PERMISSION"));
            return true;
        }
        for (int i = 0; i < SingletonManager.TOTAL_GEM_AMOUNT; i++) {
            plr.getInventory().addItem(sm.gemManager.createGem(i));
        }
        plr.sendMessage(I18N.getTranslation("ALL_GEMS_GIVEN"));
        return true;
    }
}