package dev.iseal.powergems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.iseal.powergems.gui.MainMenuGui;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class OpenMainMenuCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(I18N.getTranslation("NOT_PLAYER"));
            return true;
        }
        if (!player.hasPermission(command.getPermission())) {
            player.sendMessage(I18N.getTranslation("NO_PERMISSION"));
            return true;
        }
        MainMenuGui panel = new MainMenuGui(player);
        panel.openGemMenu();
        return true;
    }
}
