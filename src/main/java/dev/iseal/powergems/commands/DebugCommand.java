package dev.iseal.powergems.commands;

import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.I18N.I18N;
import dev.iseal.sealLib.Metrics.ConnectionManager;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DebugCommand implements CommandExecutor, TabCompleter {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final ArrayList<String> possibleTabCompletions = new ArrayList<>();

    {
        possibleTabCompletions.add("cancelCooldowns");
        possibleTabCompletions.add("resetConfig");
        possibleTabCompletions.add("forceMetricsSave");
        possibleTabCompletions.add("invalidateToken");
        possibleTabCompletions.add("dump");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player plr)) {
            sender.sendMessage(I18N.translate("NOT_PLAYER"));
            return true;
        }
        if (!plr.hasPermission(Objects.requireNonNull(command.getPermission()))) {
            plr.sendMessage(I18N.translate("NO_PERMISSION"));
            return true;
        }
        if (args.length < 1) {
            plr.sendMessage(I18N.translate("NO_SUBCOMMAND"));
            return true;
        }

        switch (args[0]) {
            case "cancelCooldowns":
                sm.cooldownManager.cancelCooldowns();
                break;
            case "resetConfig":
                sm.configManager.resetConfig();
                break;
            case "forceMetricsSave":
                sm.metricsManager.exitAndSendInfo();
                break;
            case "invalidateToken":
                ConnectionManager.getInstance().invalidateToken();
                break;
            case "dump":
                ExceptionHandler.getInstance().dumpAllClasses(null);
                break;
            default:
                plr.sendMessage(I18N.translate("INVALID_SUBCOMMAND"));
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> toReturn = new ArrayList<>();
        if (args.length == 1) {
            for (String str : possibleTabCompletions) {
                if (str.toLowerCase().contains(args[0].toLowerCase())) {
                    toReturn.add(str);
                }
            }
        }
        return toReturn;
    }
}
