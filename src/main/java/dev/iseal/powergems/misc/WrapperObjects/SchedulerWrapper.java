package dev.iseal.powergems.misc.WrapperObjects;

import com.github.sirblobman.api.folia.details.RunnableTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import static dev.iseal.powergems.PowerGems.getPlugin;

/**
 * This class makes it easy to schedule tasks without worrying about which
 * server implementation you're running on - it automatically detects Folia
 * and uses the appropriate scheduler.
 */
public class SchedulerWrapper {

    private static final long MINIMUM_FOLIA_DELAY = 1L;

    /**
     * Detects if we're running on Folia.
     */
    private boolean isFoliaServer() {
        try {
            String serverVersion = Bukkit.getServer().getVersion();
            String serverName = Bukkit.getServer().getName();

            return serverName.toLowerCase().contains("folia") ||
                    serverVersion.toLowerCase().contains("folia");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ensures the delay is compatible with Folia's requirements.
     * Folia requires delays to be at least 1 tick.
     */
    private long ensureFoliaCompatibleDelay(long delay) {
        return delay <= 0 ? MINIMUM_FOLIA_DELAY : delay;
    }

    // ========================================
    // GLOBAL TASKS (Server-wide scheduling)
    // ========================================

    /**
     * Schedules a task to run once after a delay.
     * This task runs globally and isn't tied to any specific location or entity.
     *
     * @param task         The code to execute
     * @param delayInTicks How many ticks to wait before running (20 ticks = 1 second)
     */
    public void scheduleDelayedTask(Runnable task, long delayInTicks) {
        if (isFoliaServer()) {
            long safeDelay = ensureFoliaCompatibleDelay(delayInTicks);
            Bukkit.getServer().getGlobalRegionScheduler()
                    .runDelayed(getPlugin(), scheduledTask -> task.run(), safeDelay);
        } else {
            // Handle BukkitRunnable instances specially for proper task management
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskLater(getPlugin(), delayInTicks);
            } else {
                Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), task, delayInTicks);
            }
        }
    }

    /**
     * Schedules a task to run repeatedly at fixed intervals.
     * This task runs globally and isn't tied to any specific location or entity.
     *
     * @param task                The code to execute repeatedly
     * @param initialDelayInTicks How many ticks to wait before the first execution
     * @param intervalInTicks     How many ticks to wait between each execution
     */
    public void scheduleRepeatingTask(Runnable task, long initialDelayInTicks, long intervalInTicks) {
        if (isFoliaServer()) {
            long safeDelay = ensureFoliaCompatibleDelay(initialDelayInTicks);
            Bukkit.getServer().getGlobalRegionScheduler()
                    .runAtFixedRate(getPlugin(), scheduledTask -> task.run(), safeDelay, intervalInTicks);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskTimer(getPlugin(), initialDelayInTicks, intervalInTicks);
            } else {
                Bukkit.getServer().getScheduler().runTaskTimer(getPlugin(), task, initialDelayInTicks, intervalInTicks);
            }
        }
    }

    /**
     * Schedules a repeating task and returns a handle for cancellation.
     * Useful when you need to stop the task later.
     *
     * @param task                The code to execute repeatedly
     * @param initialDelayInTicks How many ticks to wait before the first execution
     * @param intervalInTicks     How many ticks to wait between each execution
     * @return A task handle that can be used to cancel the task
     */
    public Object scheduleRepeatingTaskWithHandle(RunnableTask task, long initialDelayInTicks, long intervalInTicks) {
        if (isFoliaServer()) {
            long safeDelay = ensureFoliaCompatibleDelay(initialDelayInTicks);
            return Bukkit.getServer().getGlobalRegionScheduler()
                    .runAtFixedRate(getPlugin(), scheduledTask -> task.run(), safeDelay, intervalInTicks);
        } else {
            return Bukkit.getServer().getScheduler().runTaskTimer(getPlugin(), task::run, initialDelayInTicks, intervalInTicks);
        }
    }

    // ========================================
    // LOCATION-BASED TASKS (Region scheduling)
    // ========================================

    /**
     * Schedules a task to run in the region containing the specified location.
     * On Folia, this ensures the task runs in the correct region thread.
     * On Bukkit, this behaves like a normal delayed task.
     *
     * @param location     The location that determines which region to run in
     * @param task         The code to execute
     * @param delayInTicks How many ticks to wait before running
     */
    public void scheduleDelayedTaskAtLocation(Location location, Runnable task, long delayInTicks) {
        if (isFoliaServer()) {
            long safeDelay = ensureFoliaCompatibleDelay(delayInTicks);
            Bukkit.getServer().getRegionScheduler()
                    .runDelayed(getPlugin(), location, scheduledTask -> task.run(), safeDelay);
        } else {
            // On Bukkit, location doesn't matter for thread safety
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskLater(getPlugin(), delayInTicks);
            } else {
                Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), task, delayInTicks);
            }
        }
    }

    /**
     * Schedules a repeating task in the region containing the specified location.
     * Perfect for tasks that need to interact with blocks or entities at that location.
     *
     * @param location            The location that determines which region to run in
     * @param task                The code to execute repeatedly
     * @param initialDelayInTicks How many ticks to wait before the first execution
     * @param intervalInTicks     How many ticks to wait between each execution
     */
    public void scheduleRepeatingTaskAtLocation(Location location, Runnable task, long initialDelayInTicks, long intervalInTicks) {
        if (isFoliaServer()) {
            long safeDelay = ensureFoliaCompatibleDelay(initialDelayInTicks);
            Bukkit.getServer().getRegionScheduler()
                    .runAtFixedRate(getPlugin(), location, scheduledTask -> task.run(), safeDelay, intervalInTicks);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskTimer(getPlugin(), initialDelayInTicks, intervalInTicks);
            } else {
                Bukkit.getServer().getScheduler().runTaskTimer(getPlugin(), task, initialDelayInTicks, intervalInTicks);
            }
        }
    }

    // ========================================
    // ENTITY-BASED TASKS (Entity scheduling)
    // ========================================

    /**
     * Schedules a task to run immediately for a specific entity.
     * On Folia, this ensures thread safety when interacting with the entity.
     *
     * @param entity The entity this task is associated with
     * @param task   The code to execute
     */
    public void scheduleTaskForEntity(Entity entity, Runnable task) {
        if (isFoliaServer()) {
            // The third parameter is the retirement callback - called when entity is removed
            entity.getScheduler().run(getPlugin(), scheduledTask -> task.run(), task);
        } else {
            Bukkit.getServer().getScheduler().runTask(getPlugin(), task);
        }
    }

    /**
     * Schedules a task for an entity with a retirement callback.
     * The retirement callback runs if the entity is removed before the task completes.
     *
     * @param entity          The entity this task is associated with
     * @param task            The code to execute
     * @param onEntityRemoved Code to run if the entity is removed/retired
     * @return A task handle for cancellation
     */
    public Object scheduleTaskForEntityWithCleanup(Entity entity, Runnable task, Runnable onEntityRemoved) {
        if (isFoliaServer()) {
            return entity.getScheduler().run(getPlugin(), scheduledTask -> task.run(), onEntityRemoved);
        } else {
            // On Bukkit, entity retirement isn't a concern, so we ignore the callback
            return Bukkit.getServer().getScheduler().runTask(getPlugin(), task);
        }
    }

    /**
     * Schedules a delayed task for a specific entity.
     * Perfect for entity-specific effects or behaviors that should happen later.
     *
     * @param entity          The entity this task is associated with
     * @param task            The code to execute
     * @param delayInTicks    How many ticks to wait before running
     * @param onEntityRemoved Code to run if the entity is removed before the task executes
     * @return A task handle for cancellation
     */
    public Object scheduleDelayedTaskForEntity(Entity entity, RunnableTask task, long delayInTicks, Runnable onEntityRemoved) {
        if (isFoliaServer()) {
            long safeDelay = ensureFoliaCompatibleDelay(delayInTicks);
            return entity.getScheduler().runDelayed(getPlugin(), scheduledTask -> task.run(), onEntityRemoved, safeDelay);
        } else {
            return Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), task::run, delayInTicks);
        }
    }

    /**
     * Schedules a delayed task for a specific entity (simple version).
     *
     * @param entity          The entity this task is associated with
     * @param task            The code to execute
     * @param delayInTicks    How many ticks to wait before running
     * @param onEntityRemoved Code to run if the entity is removed before the task executes
     */
    public void scheduleDelayedTaskForEntity(Entity entity, Runnable task, long delayInTicks, Runnable onEntityRemoved) {
        if (isFoliaServer()) {
            long safeDelay = ensureFoliaCompatibleDelay(delayInTicks);
            entity.getScheduler().runDelayed(getPlugin(), scheduledTask -> task.run(), onEntityRemoved, safeDelay);
        } else {
            if (task instanceof BukkitRunnable) {
                ((BukkitRunnable) task).runTaskLater(getPlugin(), delayInTicks);
            } else {
                Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), task, delayInTicks);
            }
        }
    }

    /**
     * Schedules a repeating task for a specific entity.
     * Great for ongoing entity behaviors like particle effects or status checks.
     *
     * @param entity              The entity this task is associated with
     * @param task                The code to execute repeatedly
     * @param initialDelayInTicks How many ticks to wait before the first execution
     * @param intervalInTicks     How many ticks to wait between each execution
     * @param onEntityRemoved     Code to run if the entity is removed
     */
    public void scheduleRepeatingTaskForEntity(Entity entity, Runnable task, long initialDelayInTicks, long intervalInTicks, Runnable onEntityRemoved) {
        if (isFoliaServer()) {
            long safeDelay = ensureFoliaCompatibleDelay(initialDelayInTicks);
            entity.getScheduler().runAtFixedRate(getPlugin(), scheduledTask -> task.run(), onEntityRemoved, safeDelay, intervalInTicks);
        } else {
            Bukkit.getServer().getScheduler().runTaskTimer(getPlugin(), task, initialDelayInTicks, intervalInTicks);
        }
    }
}
