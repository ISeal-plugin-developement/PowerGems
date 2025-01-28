package dev.iseal.powergems.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.powergems.gui.GemCooldownPanel;
import dev.iseal.powergems.managers.SingletonManager;


public class PanelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                    @NotNull String[] args) {
        if (!sender.hasPermission(command.getPermission())) {
            sender.sendMessage(I18N.translate("NO_PERMISSION"));
        }

        GemCooldownPanel panel = new GemCooldownPanel
        (SingletonManager.getInstance().gemManager,
        SingletonManager.getInstance().cooldownManager);

        panel.open(sender);



        sender.sendMessage(I18N.translate("RELOADING_CONFIG"));
        SingletonManager.getInstance().configManager.reloadConfig();
        sender.sendMessage(I18N.translate("RELOADED_CONFIG"));
        return true;
    }

}
