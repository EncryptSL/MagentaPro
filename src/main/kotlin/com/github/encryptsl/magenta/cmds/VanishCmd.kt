package com.github.encryptsl.magenta.cmds

import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
class VanishCmd(private val magenta: Magenta) : AnnotationFeatures {

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        annotationParser.parse(this)
    }

    @Command("vanish")
    @Permission("magenta.vanish")
    @CommandDescription("This command enable or disable you vanish")
    fun onVanish(player: Player) {
        val user = magenta.user.getUser(player.uniqueId)

        magenta.commandHelper.doVanish(user)
        val mode = magenta.commandHelper.isVanished(user)
        player.sendMessage(magenta.locale.translation("magenta.command.vanish.success.vanish", Placeholder.parsed("mode", mode)))
    }

    @Command("vanish <player>")
    @Permission("magenta.vanish.other")
    @CommandDescription("This command enable or disable to other player vanish")
    fun onVanishOther(commandSender: CommandSender, @Argument(value = "player", suggestions = "players") target: Player) {
        val user = magenta.user.getUser(target.uniqueId)

        magenta.commandHelper.doVanish(user)

        val mode = magenta.commandHelper.isVanished(user)

        target.sendMessage(magenta.locale.translation("magenta.command.vanish.success.vanish", Placeholder.parsed("mode", mode)))

        commandSender.sendMessage(magenta.locale.translation("magenta.command.vanish.success.vanish.to", TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.parsed("mode", mode)
        )))
    }

}