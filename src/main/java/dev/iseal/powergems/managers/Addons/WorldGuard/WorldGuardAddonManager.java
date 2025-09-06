package dev.iseal.powergems.managers.Addons.WorldGuard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import dev.iseal.powergems.PowerGems;
import dev.iseal.sealLib.Systems.I18N.I18N;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldGuardAddonManager {
    private static WorldGuardAddonManager instance = null;
    public static WorldGuardAddonManager getInstance() {
        if (instance == null) {
            instance = new WorldGuardAddonManager();
        }
        return instance;
    }

    private final WorldGuard worldGuard = WorldGuard.getInstance();
    public StateFlag GEMS_ENABLED_FLAG = null;
    private final Logger logger = PowerGems.getPlugin().getLogger();

    public void init() {
        logger.info(I18N.translate("ATTEMPT_REGISTER_WG_FLAG"));
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            // create flag
            StateFlag flag = new StateFlag("powergems-gems-enabled", true);
            registry.register(flag);
            GEMS_ENABLED_FLAG = flag; // only set var if it didn't crash already
            logger.info(I18N.translate("WG_FLAG_REGISTERED_SUCCESS"));
        } catch (FlagConflictException e) {
            // some other plugin registered a flag with the same name already.
            // use the existing flag, and hope it doesn't crash - be sure to check type
            Flag<?> existing = registry.get("powergems-gems-enabled");
            if (existing instanceof StateFlag) {
                GEMS_ENABLED_FLAG = (StateFlag) existing;
            } else {
                // types don't match - we're fucked, throw exception
                logger.severe(I18N.translate("ATTEMPT_REGISTER_WG_FLAG_FAILED"));
                ExceptionHandler.getInstance().dealWithException(e, Level.SEVERE, "WG_FLAG_REGISTERING_FAILED_WRONG_TYPES", registry);
            }
        } catch (IllegalStateException ex) {
            ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "WG_FLAG_REGISTERING_FAILED_WRONG_TIME", registry);
        }
    }

    public boolean isGemUsageAllowedInRegion(Player p) {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
        RegionQuery query = regionContainer.createQuery();
        return query.testState(BukkitAdapter.adapt(p.getLocation()), localPlayer, GEMS_ENABLED_FLAG);
    }

}
