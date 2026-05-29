package dev.iseal.powergems.commands;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClearPlayerGemsCommand implements CommandExecutor, TabCompleter {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final ArrayList<String> possibleTabCompletions = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(command.getPermission())) {
            sender.sendMessage(Component.text(I18N.translate("NO_PERMISSION")));
            return true;
        }

        Player targetPlayer = null;
        if (args.length >= 1) {
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(Component.text("Player not found.").color(NamedTextColor.DARK_RED));
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text(I18N.translate("NOT_PLAYER")));
                return true;
            }
            targetPlayer = (Player) sender;
        }

        Integer typeId = null;
        Integer targetLevel = null;

        if (args.length >= 2) {
            String arg1 = args[1].toLowerCase();
            if (!arg1.equals("all")) {
                int id = GemManager.lookUpID(arg1);
                if (id != -1) {
                    typeId = id;
                } else if (isNumber(arg1)) {
                    if (args.length >= 3) {
                        typeId = Integer.parseInt(arg1);
                    } else {
                        targetLevel = Integer.parseInt(arg1);
                    }
                } else {
                    sender.sendMessage(Component.text("Invalid gem type or level.").color(NamedTextColor.DARK_RED));
                    return true;
                }
            }
        }

        if (args.length >= 3) {
            String arg2 = args[2].toLowerCase();
            if (!arg2.equals("all")) {
                if (isNumber(arg2)) {
                    targetLevel = Integer.parseInt(arg2);
                } else {
                    sender.sendMessage(Component.text("Invalid level.").color(NamedTextColor.DARK_RED));
                    return true;
                }
            }
        }

        ArrayList<ItemStack> gems = sm.gemManager.getPlayerGems(targetPlayer);
        int removedCount = 0;
        for (ItemStack gem : gems) {
            boolean matchType = true;
            boolean matchLevel = true;

            if (typeId != null) {
                String name = sm.gemManager.getName(gem);
                int gemId = GemManager.lookUpID(name);
                if (gemId != typeId) {
                    matchType = false;
                }
            }

            if (targetLevel != null) {
                int lvl = sm.gemManager.getLevel(gem);
                if (lvl != targetLevel) {
                    matchLevel = false;
                }
            }

            if (matchType && matchLevel) {
                targetPlayer.getInventory().remove(gem);
                removedCount++;
            }
        }

        sender.sendMessage(Component.text("Successfully removed " + removedCount + " gem(s) from " + targetPlayer.getName() + ".").color(NamedTextColor.GREEN));
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

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (possibleTabCompletions.isEmpty()) {
            IntStream.range(0, SingletonManager.TOTAL_GEM_AMOUNT)
                    .mapToObj(GemManager::lookUpName)
                    .forEach(possibleTabCompletions::add);
        }

        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            List<String> completions = new ArrayList<>(possibleTabCompletions);
            completions.add("all");
            for (int i = 1; i <= 5; i++) {
                completions.add(String.valueOf(i));
            }
            return completions.stream()
                    .filter(str -> str.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 3) {
            List<String> completions = new ArrayList<>();
            completions.add("all");
            for (int i = 1; i <= 5; i++) {
                completions.add(String.valueOf(i));
            }
            return completions.stream()
                    .filter(str -> str.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
