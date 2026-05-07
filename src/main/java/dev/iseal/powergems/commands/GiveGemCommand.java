package dev.iseal.powergems.commands;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GiveGemCommand implements CommandExecutor, TabCompleter {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final ArrayList<String> possibleTabCompletions = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] args) {
        if (!sender.hasPermission(command.getPermission())) {
            sender.sendMessage(I18N.translate("NO_PERMISSION"));
            return true;
        }

        Player targetPlayer = null;
        if (args.length > 2) {
            targetPlayer = Bukkit.getPlayer(args[args.length - 1]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
                return true;
            }
        }

        if (targetPlayer == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(I18N.translate("NOT_PLAYER"));
                return true;
            }
            targetPlayer = (Player) sender;
        }

        if (args.length < 1) {
            targetPlayer.getInventory().addItem(sm.gemManager.createGem());
            return true;
        }

        String gemNumString = args[0];
        int gemLevel = 1;
        boolean useDefaultLevel = false;
        if (args.length >= 2) {
            if ("default".equalsIgnoreCase(args[1])) {
                useDefaultLevel = true;
            } else if (isNumber(args[1])) {
                gemLevel = Integer.parseInt(args[1]);
            }
        }

        if (isNumber(gemNumString)) {
            if (useDefaultLevel) {
                targetPlayer.getInventory().addItem(sm.gemManager.createGem(Integer.parseInt(gemNumString)));
            } else {
                targetPlayer.getInventory().addItem(sm.gemManager.createGem(Integer.parseInt(gemNumString), gemLevel));
            }
            return true;
        }

        int gemId = GemManager.lookUpID(gemNumString);
        if (gemId != -1) {
            if (useDefaultLevel) {
                targetPlayer.getInventory().addItem(sm.gemManager.createGem(gemId));
            } else {
                targetPlayer.getInventory().addItem(sm.gemManager.createGem(gemId, gemLevel));
            }
            return true;
        }

        sender.sendMessage(Component.text("Invalid gem name / ID.").color(NamedTextColor.DARK_RED));
        return true;
    }

    private boolean isNumber(String num) {
        try {
            return Integer.parseInt(num) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                                      @NotNull String[] args) {
        if (possibleTabCompletions.isEmpty()) {
            IntStream.range(0, SingletonManager.TOTAL_GEM_AMOUNT)
                    .mapToObj(GemManager::lookUpName)
                    .forEach(possibleTabCompletions::add);
        }

        if (args.length == 1) {
            return possibleTabCompletions.stream()
                    .filter(str -> str.toLowerCase().contains(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return List.of("<level>");
        } else if (args.length > 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}