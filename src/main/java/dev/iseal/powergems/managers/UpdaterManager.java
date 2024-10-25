package dev.iseal.powergems.managers;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import dev.iseal.powergems.PowerGems;
import dev.iseal.sealLib.I18N.I18N;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

/**
 * This class manages the update checking process for the PowerGems plugin.
 * It extends the Thread class, allowing it to perform update checks in the
 * background.
 */
public class UpdaterManager extends Thread {

    private static UpdaterManager instance = null;
    public static UpdaterManager getInstance() {
        if (instance == null) {
            instance = new UpdaterManager();
        }
        return instance;
    }

    // The UpdateChecker object used to perform update checks
    private UpdateChecker uc = null;
    private final Logger l = Bukkit.getServer().getLogger();

    /**
     * The run method is called when the thread is started.
     * It initializes the UpdateChecker object and starts the update checking
     * process.
     */
    public void run() {
        uc = new UpdateChecker(PowerGems.getPlugin(), UpdateCheckSource.SPIGOT, "108943")
                .setDownloadLink("https://www.spigotmc.org/resources/1-19-1-20-4-powergems.108943/")
                .setNotifyByPermissionOnJoin("powergems.check_update")
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                .checkEveryXHours(1) // check every hour
                .setColoredConsoleOutput(true)
                .setUsingPaidVersion(false)
                .setTimeout(3000)
                .onFail((commandSenders, e) -> handleResult(commandSenders, e, true))
                .onSuccess((commandSenders, s) -> handleResult(commandSenders, null, false))
                .checkNow(); // And check right now
        l.info("[PowerGems] "+ I18N.translate("RUNNING_UPDATE_CHECK"));
    }

    /**
     * This method is called when the update check is completed.
     * It logs a message to the console indicating the result of the update check.
     *
     * @param commandSenders The CommandSender objects that requested the update
     *                       check
     * @param e              The Exception object if the update check failed, or
     *                       null if it was successful
     * @param isFail         A boolean indicating whether the update check failed
     */
    private void handleResult(CommandSender[] commandSenders, Exception e, boolean isFail) {
        l.info(I18N.translate("UPDATE_CHECK_COMPLETED")
                .replace("{result}", isFail ? I18N.translate("FAIL") : I18N.translate("SUCCESS"))
                .replace("{error}", e == null ? "none" : e.getMessage())
                .replace("{issuer}", commandSenders[0].getName()));
        if (isFail) {
            l.warning("Update test failed with error " + e.getMessage());
        }
    }

    /**
     * This method starts the update checking process.
     * It logs a message to the console and calls the checkNow method on the
     * UpdateChecker object.
     *
     * @param sender The CommandSender object that requested the update check
     */
    public void startUpdate(CommandSender sender) {
        l.info("[PowerGems] "+ I18N.translate("UPDATE_CHECK_STARTED")
                .replace("{issuer}", sender.getName())
        );
        uc.checkNow(sender);
    }
}
