package dev.iseal.powergems.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.iseal.powergems.managers.GemManager;
import dev.iseal.powergems.managers.SingletonManager;
import dev.iseal.sealLib.Systems.I18N.I18N;

public class GiveGemCommand implements CommandExecutor, TabCompleter {

    private final SingletonManager sm = SingletonManager.getInstance();
    private final ArrayList<String> possibleTabCompletions = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(I18N.getTranslation("NOT_PLAYER"));
            return true;
        }
        
        if (!player.hasPermission(command.getPermission())) {
            player.sendMessage(I18N.getTranslation("NO_PERMISSION"));
            return true;
        }

        if (args.length < 1) {
            player.getInventory().addItem(sm.gemManager.createGem());
            return true;
        }

        String gemNumString = args[0];
        int gemLevel = args.length >= 2 && isNumber(args[1]) ? Integer.parseInt(args[1]) : 1;
        
        if (isNumber(gemNumString)) {
            player.getInventory().addItem(sm.gemManager.createGem(Integer.parseInt(gemNumString), gemLevel));
            return true;
        }
        
        int gemId = GemManager.lookUpID(gemNumString);
        if (gemId != -1) {
            player.getInventory().addItem(sm.gemManager.createGem(gemId, gemLevel));
            return true;
        }
        
        player.sendMessage(ChatColor.DARK_RED + "Invalid gem name / ID.");
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
                    .toList();
        }
        return new ArrayList<>();
    }
}
