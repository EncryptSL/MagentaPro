package com.github.encryptsl.magenta.api.scheduler

import com.github.encryptsl.magenta.Magenta
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.plugin.Plugin
import java.util.function.Consumer

class SchedulerMagenta(private val magenta: Magenta) : AbstractSchedulerMagenta() {
}