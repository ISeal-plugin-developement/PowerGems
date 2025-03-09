package dev.iseal.powergems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import dev.iseal.powergems.gui.GemCooldownPanel;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;

/**
 * Command executor for the panel command that displays the Gem Cooldown Manager GUI.
 * This command allows players to view and manage their gem cooldowns through a graphical interface.
 */
public class PanelCommand implements CommandExecutor {

    private final GemCooldownPanel panel;

    /**
     * Constructs a new PanelCommand with necessary manager instances.
     */
    public PanelCommand() {
        SingletonManager sm = SingletonManager.getInstance();
        this.panel = new GemCooldownPanel();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(I18N.translate("NOT_PLAYER"));
            return true;
        }

        // Check permissions
        if (!sender.hasPermission(command.getPermission())) {
            sender.sendMessage(I18N.translate("NO_PERMISSION"));
            return true;
        }

        // Open the panel for the player
        panel.open(sender);
        return true;
    }
}
