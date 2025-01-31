package dev.iseal.powergems.managers;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import dev.iseal.powergems.PowerGems;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.sealLib.Updater.UpdateChecker;

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
        // check interval is 1hr (20 ticks * 60 seconds * 60 minutes)
        uc = new UpdateChecker("kCgEfc6s", PowerGems.getPlugin(), "powergems.notify", 20*60*60, this::handleResult, this::handleResult);
        l.info("[PowerGems] "+ I18N.translate("RUNNING_UPDATE_CHECK"));
    }

    private void handleResult(String latestVer, String sender) {
        handleResult(sender, null, false, latestVer);
    }

    private void handleResult(Exception e) {
        handleResult(Bukkit.getConsoleSender().getName(), e, true, "");
    }



    /**
     * This method is called when the update check is completed.
     * It logs a message to the console indicating the result of the update check.
     *
     * @param commandSender The sender that requested the update
     *                       check
     * @param e              The Exception object if the update check failed, or
     *                       null if it was successful
     * @param isFail         A boolean indicating whether the update check failed
     */
    private void handleResult(String commandSender, Exception e, boolean isFail, String latestVer) {
        l.info(I18N.translate("UPDATE_CHECK_COMPLETED")
                .replace("{result}", isFail ? I18N.translate("FAIL") : I18N.translate("SUCCESS"))
                .replace("{error}", e == null ? "none" : e.getMessage())
                .replace("{issuer}", commandSender)
                .replace("{latestVer}", latestVer));
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
        uc.check(sender.getName());
    }
}
