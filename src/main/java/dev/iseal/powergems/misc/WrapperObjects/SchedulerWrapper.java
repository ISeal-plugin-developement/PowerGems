package dev.iseal.powergems.misc.WrapperObjects;

import com.github.sirblobman.api.folia.details.RunnableTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import static dev.iseal.powergems.PowerGems.getPlugin;
//NOTE: This class has fully AI generated documentation.

/**
 * Wrapper class for scheduling tasks with compatibility between Bukkit and Folia schedulers.
 * Automatically detects if running on Folia and uses appropriate scheduler implementation.
 * <p>
 * Supports:
 * - Global scheduling (for tasks not tied to specific regions/entities)
 * - Region scheduling (for tasks tied to specific world locations)
 * - Entity scheduling (for tasks tied to specific entities)
 */
public class SchedulerWrapper {
    public SchedulerWrapper(Plugin plugin) {
    }

    /**
     * Checks if the server is running Folia by checking server implementation
     *
     * @return true if Folia is detected and active, false otherwise
     */
    private boolean checkIsFolia() {
        try {
            String serverVersion = Bukkit.getServer().getVersion();
            String serverName = Bukkit.getServer().getName();

            return serverName.toLowerCase().contains("folia") ||
                   serverVersion.toLowerCase().contains("folia");
        } catch (Exception e) {
            return false;
        }
    }

    // ===== GLOBAL SCHEDULING =====


    /**
     * Runs a task after a specified delay on the global scheduler
     *
     * @param task  The task to run (Runnable)
     * @param delay Delay in ticks before execution
     */
    public void runTaskLater(Runnable task, long delay) {
        if (checkIsFolia()) {
            long foliaDelay = delay <= 0 ? 1 : delay;
            Bukkit.getServer().getGlobalRegionScheduler().runDelayed(getPlugin(), scheduledTask -> task.run(), foliaDelay);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskLater(getPlugin(), delay);
            } else {
                Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), task, delay);
            }
        }
    }

    /**
     * Runs a repeating task on the global scheduler
     *
     * @param task   The task to run repeatedly
     * @param delay  Initial delay in ticks before first execution
     * @param period Period in ticks between executions
     */
    public void runTaskTimer(Runnable task, long delay, long period) {
        if (checkIsFolia()) {
            long foliaDelay = delay <= 0 ? 1 : delay;
            Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(getPlugin(), scheduledTask -> task.run(), foliaDelay, period);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskTimer(getPlugin(), delay, period);
            } else {
                Bukkit.getServer().getScheduler().runTaskTimer(getPlugin(), task, delay, period);
            }
        }
    }

    /**
     * Runs a repeating task on the global scheduler (returns Object)
     *
     * @param task   The task to run repeatedly
     * @param delay  Initial delay in ticks before first execution
     * @param period Period in ticks between executions
     * @return The scheduled task object
     */
    public Object runTaskTimer(RunnableTask task, long delay, long period) {
        if (checkIsFolia()) {
            long foliaDelay = delay <= 0 ? 1 : delay;
            return Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(getPlugin(), scheduledTask -> task.run(), foliaDelay, period);
        } else {
            return Bukkit.getServer().getScheduler().runTaskTimer(getPlugin(), task::run, delay, period);
        }
    }

    // ===== REGION SCHEDULING =====

    /**
     * Runs a task after a delay in the region containing the specified location
     *
     * @param location The location to determine the region
     * @param task     The task to run (Runnable)
     * @param delay    Delay in ticks before execution
     */
    public void runTaskLaterAtLocation(Location location, Runnable task, long delay) {
        if (checkIsFolia()) {
            long foliaDelay = delay <= 0 ? 1 : delay;
            Bukkit.getServer().getRegionScheduler().runDelayed(getPlugin(), location, scheduledTask -> task.run(), foliaDelay);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskLater(getPlugin(), delay);
            } else {
                Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), task, delay);
            }
        }
    }


    /**
     * Runs a repeating task in the region containing the specified location
     *
     * @param location The location to determine the region
     * @param task     The task to run repeatedly (Runnable)
     * @param delay    Initial delay in ticks before first execution
     * @param period   Period in ticks between executions
     */
    public void runTaskTimerAtLocation(Location location, Runnable task, long delay, long period) {
        if (checkIsFolia()) {
            // Folia doesn't allow delay of 0, so use 1 tick minimum
            long foliaDelay = delay <= 0 ? 1 : delay;
            Bukkit.getServer().getRegionScheduler().runAtFixedRate(getPlugin(), location, scheduledTask -> task.run(), foliaDelay, period);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskTimer(getPlugin(), delay, period);
            } else {
                Bukkit.getServer().getScheduler().runTaskTimer(getPlugin(), task, delay, period);
            }
        }
    }

    // ===== ENTITY SCHEDULING =====

    /**
     * Runs a task immediately for the specified entity
     *
     * @param entity The entity to schedule the task for
     * @param task   Callback for when entity is retired/removed
     */
    public void runTaskForEntity(Entity entity, Runnable task) {
        if (checkIsFolia()) {
            entity.getScheduler().run(getPlugin(), scheduledTask -> task.run(), task);
        } else {
            Bukkit.getServer().getScheduler().runTask(getPlugin(), task);
        }
    }

    /**
     * Runs a task immediately for the specified entity
     *
     * @param entity  The entity to schedule the task for
     * @param task    The task to run (Runnable)
     * @param retired Callback for when entity is retired/removed
     * @return The scheduled task object
     */
    public Object runTaskForEntity(Entity entity, Runnable task, Runnable retired) {
        if (checkIsFolia()) {
            return entity.getScheduler().run(getPlugin(), scheduledTask -> task.run(), retired);
        } else {
            return Bukkit.getServer().getScheduler().runTask(getPlugin(), task);
        }
    }

    /**
     * Runs a task after a delay for the specified entity
     *
     * @param entity  The entity to schedule the task for
     * @param task    The task to run
     * @param delay   Delay in ticks before execution
     * @param retired Callback for when entity is retired/removed
     * @return The scheduled task object
     */
    public Object runTaskLaterForEntity(Entity entity, RunnableTask task, long delay, Runnable retired) {
        if (checkIsFolia()) {
            long foliaDelay = delay <= 0 ? 1 : delay;
            return entity.getScheduler().runDelayed(getPlugin(), scheduledTask -> task.run(), retired, foliaDelay);
        } else {
            return Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), task::run, delay);
        }
    }

    /**
     * Runs a task after a delay for the specified entity
     *
     * @param entity  The entity to schedule the task for
     * @param task    The task to run (Runnable)
     * @param delay   Delay in ticks before execution
     * @param retired Callback for when entity is retired/removed
     */
    public void runTaskLaterForEntity(Entity entity, Runnable task, long delay, Runnable retired) {
        if (checkIsFolia()) {
            long foliaDelay = delay <= 0 ? 1 : delay;
            entity.getScheduler().runDelayed(getPlugin(), scheduledTask -> task.run(), retired, foliaDelay);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskLater(getPlugin(), delay);
            } else {
                Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), task, delay);
            }
        }
    }


    /**
     * Runs a repeating task for the specified entity
     *
     * @param entity  The entity to schedule the task for
     * @param task    The task to run repeatedly (Runnable)
     * @param delay   Initial delay in ticks before first execution
     * @param period  Period in ticks between executions
     * @param retired Callback for when entity is retired/removed
     */
    public void runTaskTimerForEntity(Entity entity, Runnable task, long delay, long period, Runnable retired) {
        if (checkIsFolia()) {
            long foliaDelay = delay <= 0 ? 1 : delay;
            entity.getScheduler().runAtFixedRate(getPlugin(), scheduledTask -> task.run(), retired, foliaDelay, period);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskTimer(getPlugin(), delay, period);
            } else {
                Bukkit.getServer().getScheduler().runTaskTimer(getPlugin(), task, delay, period);
            }
        }
    }
}
