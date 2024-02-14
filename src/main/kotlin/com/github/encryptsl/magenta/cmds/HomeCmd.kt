package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.HomeCreateEvent
import com.github.encryptsl.magenta.api.events.home.HomeDeleteEvent
import com.github.encryptsl.magenta.api.events.home.HomeRenameEvent
import com.github.encryptsl.magenta.api.events.home.HomeTeleportEvent
import com.github.encryptsl.magenta.api.menu.home.HomeGUI
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class HomeCmd(private val magenta: Magenta) {

    private val homeMenuGUI: HomeGUI by lazy { HomeGUI(magenta) }

    @Command("home <home>")
    @Permission("magenta.home")
    fun onHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home, magenta.config.getLong("teleport-cooldown")))
        }
    }

    @Command("sethome <home>")
    @Permission("magenta.sethome")
    fun onSetHome(player: Player, @Argument(value = "home") home: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeCreateEvent(player, player.location, home))
        }
    }

    @Command("sethomeicon <home> <icon>")
    @Permission("magenta.sethomeicon")
    fun onSetHomeIcon(player: Player, @Argument(value = "home", suggestions = "homes") home: String, @Argument("icon", suggestions = "homeIcons") icon: String) {
        magenta.homeModel.setHomeIcon(player, home, icon)
    }

    @Command("delhome <home>")
    @Permission("magenta.delhome")
    fun onDeleteHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeDeleteEvent(player, home))
        }
    }

    @Command("renamehome <oldName> <newName>")
    @Permission("magenta.rename.home")
    fun onRenameHome(player: Player, @Argument(value = "oldName") oldName: String, @Argument(value = "newName") newName: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeRenameEvent(player, oldName, newName))
        }
    }

    @Command("homes")
    @Permission("magenta.home.list")
    fun onHomeList(player: Player) {
        homeMenuGUI.openHomeGUI(player)
        /*SchedulerMagenta.doSync(magenta) {
            magenta.pluginManager.callEvent(HomeInfoEvent(player, null, InfoType.LIST))
        }*/
    }
}