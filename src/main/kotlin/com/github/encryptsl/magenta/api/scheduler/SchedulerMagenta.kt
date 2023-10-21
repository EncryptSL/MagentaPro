package com.github.encryptsl.magenta.api.scheduler

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

class SchedulerMagenta : IScheduler {
    override fun runAtFixedRate(
        plugin: Plugin,
        scheduledTask: Consumer<ScheduledTask>,
        initialDelay: Long,
        period: Long,
        timeUnit: TimeUnit
    ) {
        Bukkit.getAsyncScheduler().runAtFixedRate(plugin, scheduledTask, initialDelay, period, timeUnit)
    }

    override fun delayedTask(plugin: Plugin, scheduledTask: Consumer<ScheduledTask>, initialDelay: Long, timeUnit: TimeUnit) {
        Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask, initialDelay, timeUnit)
    }

    override fun delayedTask(plugin: Plugin, runnable: Runnable, delay: Long) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delay)
    }

    override fun runTaskTimerSync(plugin: Plugin, runnable: Runnable, initialDelay: Long, period: Long) {
        Bukkit.getScheduler().runTaskTimer(plugin, runnable, initialDelay, period)
    }

    override fun runTaskTimerAsync(plugin: Plugin, runnable: Runnable, initialDelay: Long, period: Long) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, initialDelay, period)
    }

    override fun runTaskTimerAsyncTask(
        plugin: Plugin,
        runnable: Runnable,
        initialDelay: Long,
        period: Long
    ): BukkitTask {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, initialDelay, period)
    }

    override fun runTaskTimerSyncTask(
        plugin: Plugin,
        runnable: Runnable,
        initialDelay: Long,
        period: Long
    ): BukkitTask {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, initialDelay, period)
    }

    override fun doAsync(plugin: Plugin, runnable: Runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
    }
    override fun doSync(plugin: Plugin, runnable: Runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable)
    }
}