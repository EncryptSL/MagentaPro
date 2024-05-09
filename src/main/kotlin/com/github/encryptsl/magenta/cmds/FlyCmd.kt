package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.Permissions
import com.github.encryptsl.magenta.common.hook.luckperms.LuckPermsAPI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.*
import org.incendo.cloud.paper.PaperCommandManager

@Suppress("UNUSED")
class FlyCmd(private val magenta: Magenta) : AnnotationFeatures {

    private val luckPermsAPI: LuckPermsAPI by lazy { LuckPermsAPI() }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: PaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("fly")
    @Permission("magenta.fly")
    @CommandDescription("This command enable or disable your flying")
    fun onFlySelf(player: Player) {
        magenta.commandHelper.allowFly(null, player)
    }

    @Command("fly <target>")
    @Permission("magenta.fly.other")
    @CommandDescription("This command enable or disable other player flying")
    fun onFlyTarget(commandSender: CommandSender, @Argument(value = "target", suggestions = "players") target: Player) {

        if (target.hasPermission(Permissions.FLY_MODIFY_EXEMPT)) return

        magenta.commandHelper.allowFly(commandSender, target)
    }

}