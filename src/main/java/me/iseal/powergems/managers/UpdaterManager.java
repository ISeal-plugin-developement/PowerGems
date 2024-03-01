package me.iseal.powergems.managers;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import me.iseal.powergems.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

public class UpdaterManager extends Thread{
    UpdateChecker uc = null;
    Logger l = Bukkit.getServer().getLogger();
    private final String checkRequestedByString = "Performing update check requested by ";
    private final String checkDoneString = "Update check requested by ";


    public void run(){
        uc = new UpdateChecker(Main.getPlugin(), UpdateCheckSource.SPIGOT, "108943")
                .setDownloadLink("https://www.spigotmc.org/resources/1-19-1-20-4-powergems.108943/")
                .setNotifyByPermissionOnJoin("powergems.check_update")
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                .checkEveryXHours(1) //check every hour
                .setColoredConsoleOutput(true)
                .setUsingPaidVersion(false)
                .setTimeout(3000)
                .onFail((commandSenders, e) ->  handleResult(commandSenders, e, true))
                .onSuccess((commandSenders, s) -> handleResult(commandSenders, null, false))
                .checkNow(); // And check right now
        l.info("Running update check");
    }

    private void handleResult(CommandSender[] commandSenders, Exception e, boolean isFail){
        String doneByString = "";
        for (CommandSender cmdsnr : commandSenders){
            doneByString = doneByString+cmdsnr.getName()+" ";
        }
        l.info(checkDoneString+doneByString+(isFail ? "Failed" : "Successful"));
        if (isFail){
            l.warning("Update test failed with error "+e.getMessage());
        }
    }
    public void startUpdate(CommandSender sender){
        l.info(checkRequestedByString+sender.getName());
        uc.checkNow(sender);
    }
}
