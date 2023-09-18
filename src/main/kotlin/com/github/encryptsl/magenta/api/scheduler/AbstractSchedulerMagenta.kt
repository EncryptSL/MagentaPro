package com.github.encryptsl.magenta.api.scheduler

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

abstract class AbstractSchedulerMagenta : IScheduler {

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

    override fun runTaskTimeSync(plugin: Plugin, runnable: Runnable, initialDelay: Long, period: Long) {
        Bukkit.getScheduler().runTaskTimer(plugin, runnable, initialDelay, period)
    }

    override fun runTaskTimerAsync(plugin: Plugin, runnable: Runnable, initialDelay: Long, period: Long) {
        Bukkit.getScheduler().runTaskTimer(plugin, runnable, initialDelay, period)
    }

    override fun runTask(plugin: Plugin, runnable: Runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable)
    }

}