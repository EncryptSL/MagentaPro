package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.events.home.*
import com.github.encryptsl.magenta.api.menu.home.HomeGUI
import com.github.encryptsl.magenta.api.scheduler.SchedulerMagenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
class HomeCmd(private val magenta: Magenta) {

    private val homeMenuGUI: HomeGUI by lazy { HomeGUI(magenta) }

    @Command("home <home>")
    @Permission("magenta.home")
    @CommandDescription("This command teleport you into your home")
    fun onHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeTeleportEvent(player, home, magenta.config.getLong("teleport-cooldown")))
        }
    }

    @Command("sethome <home>")
    @Permission("magenta.sethome")
    @CommandDescription("This command create your home where you stand.")
    fun onSetHome(player: Player, @Argument(value = "home") home: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeCreateEvent(player, player.location, home))
        }
    }

    @Command("sethomeicon|sethicon <home> <icon>")
    @Permission("magenta.sethomeicon")
    @CommandDescription("This command set home icon visible in your gui homelist")
    fun onSetHomeIcon(player: Player, @Argument(value = "home", suggestions = "homes") home: String, @Argument("icon", suggestions = "homeIcons") icon: String) {
        magenta.homeModel.setHomeIcon(player, home, icon)

        player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.change.icon"),
            TagResolver.resolver(
                Placeholder.parsed("home", home),
                Placeholder.parsed("icon", icon))
        ))
    }

    @Command("movehome|mhome <home>")
    @Permission("magenta.movehome")
    @CommandDescription("This command move your home to location where you stand.")
    fun onMoveHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeMoveLocationEvent(player, player.location, home))
        }
    }

    @Command("delhome|dhome <home>")
    @Permission("magenta.delhome")
    @CommandDescription("This command delete your home.")
    fun onDeleteHome(player: Player, @Argument(value = "home", suggestions = "homes") home: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeDeleteEvent(player, home))
        }
    }

    @Command("renamehome|rhome <oldName> <newName>")
    @Permission("magenta.rename.home")
    @CommandDescription("This command rename your home.")
    fun onRenameHome(player: Player, @Argument(value = "oldName") oldName: String, @Argument(value = "newName") newName: String) {
        SchedulerMagenta.doSync(magenta) {
            magenta.server.pluginManager.callEvent(HomeRenameEvent(player, oldName, newName))
        }
    }

    @Command("homes|homelist")
    @Permission("magenta.home.list")
    @CommandDescription("This command open gui list or chat list.")
    fun onHomeList(player: Player) {
        homeMenuGUI.openHomeGUI(player)
    }
}