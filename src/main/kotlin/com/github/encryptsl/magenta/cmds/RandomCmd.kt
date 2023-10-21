package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class RandomCmd(private val magenta: Magenta) {

    @CommandMethod("random tag <type> <player>")
    @CommandPermission("magenta.random.tag")
    fun onRandomTag(
        commandSender: CommandSender,
        @Argument(value = "type", suggestions = "tagCategories") type: String,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        val tags: List<String> = magenta.tags.getConfig().getStringList("categories.$type")
        val randomTag = tags.random()
        if (!target.hasPermission(randomTag)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user ${target.name} permission set $randomTag")
            target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.random.tag.success.player"),
                Placeholder.parsed("category", type)
            ))
            return
        }
        magenta.logger.info("Hráč ${target.name} již $randomTag oprávnění vlastní proto mu byl nabídnut jiný tag !")
        magenta.schedulerMagenta.doSync(magenta) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                magenta.stringUtils.magentaPlaceholders("lp user %player% permission set ${tags.random()}", target)
            )
        }
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.random.tag.success.player"),
            Placeholder.parsed("category", type)
        ))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.random.tag.success.to"), TagResolver.resolver(
            Placeholder.parsed("category", type),
            Placeholder.parsed("tag", randomTag)
        )))
    }

}