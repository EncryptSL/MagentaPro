package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class InvseeCmd(private val magenta: Magenta) {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    @Command("invsee <target>")
    @Permission("magenta.invsee")
    fun onInvseePlayer(player: Player, @Argument(value = "target", suggestions = "players") target: Player) {
        if (luckPermsAPI.hasPermission(target, "magenta.invsee.exempt"))
            return

        SchedulerMagenta.doSync(magenta) {
            player.openInventory(target.inventory)
        }
    }
}