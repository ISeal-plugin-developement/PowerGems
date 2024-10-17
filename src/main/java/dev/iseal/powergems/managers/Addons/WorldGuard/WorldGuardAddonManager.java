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
import dev.iseal.sealLib.Utils.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class WorldGuardAddonManager {
    private static WorldGuardAddonManager instance = null;
    public static WorldGuardAddonManager getInstance() {
        if (instance == null) {
            instance = new WorldGuardAddonManager();
        }
        return instance;
    }

    private WorldGuard worldGuard = WorldGuard.getInstance();
    public StateFlag GEMS_ENABLED_FLAG = null;

    public void init() {
        Bukkit.getServer().getLogger().info("[PowerGems] Attempting to register WorldGuard flag");
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            // create flag
            StateFlag flag = new StateFlag("powergems-gems-enabled", true);
            registry.register(flag);
            GEMS_ENABLED_FLAG = flag; // only set var if it didn't crash already
        } catch (FlagConflictException e) {
            // some other plugin registered a flag with the same name already.
            // use the existing flag, and hope it doesn't crash - be sure to check type
            Flag<?> existing = registry.get("powergems-gems-enabled");
            if (existing instanceof StateFlag) {
                GEMS_ENABLED_FLAG = (StateFlag) existing;
            } else {
                // types don't match - we're fucked, throw exception or stay silent?
                // I guess just logging will do
                Bukkit.getLogger().severe("[PowerGems] Attempting to register the flag has yielded bad results, do you have a conflicting plugin?");
            }
        } catch (IllegalStateException ex) {
            ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "FLAG_REGISTERING_FAILED", registry, System.currentTimeMillis());
        }
    }

    public boolean isGemUsageAllowedInRegion(Player p) {
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
        RegionQuery query = regionContainer.createQuery();
        return query.testState(BukkitAdapter.adapt(p.getLocation()), localPlayer, GEMS_ENABLED_FLAG);
    }

}
