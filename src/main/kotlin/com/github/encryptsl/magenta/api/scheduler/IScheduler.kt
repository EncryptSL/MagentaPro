package com.github.encryptsl.magenta.api.scheduler

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

interface IScheduler {

    fun runAtFixedRate(plugin: Plugin, scheduledTask: Consumer<ScheduledTask>, initialDelay: Long, period: Long, timeUnit: TimeUnit)
    fun delayedTask(plugin: Plugin, scheduledTask: Consumer<ScheduledTask>, initialDelay: Long, timeUnit: TimeUnit)

    fun delayedTask(plugin: Plugin, runnable: Runnable, delay: Long)
    fun runTaskTimerAsync(plugin: Plugin, runnable: Runnable, initialDelay: Long, period: Long)

    fun runTaskTimerSync(plugin: Plugin, runnable: Runnable, initialDelay: Long, period: Long)

    fun runTaskTimerAsyncTask(plugin: Plugin, runnable: Runnable, initialDelay: Long, period: Long): BukkitTask

    fun runTaskTimerSyncTask(plugin: Plugin, runnable: Runnable, initialDelay: Long, period: Long): BukkitTask

    fun doAsync(plugin: Plugin, runnable: Runnable)
    fun doSync(plugin: Plugin, runnable: Runnable)

}