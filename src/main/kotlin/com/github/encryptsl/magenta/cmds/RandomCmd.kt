package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.specifier.Range
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin MagentaPro")
class RandomCmd(private val magenta: Magenta) {

    @CommandMethod("random crates key <player> [amount]")
    @CommandPermission("magenta.random.crates.key")
    fun onRandomCratesKey(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "players") target: Player,
        @Argument("amount", defaultValue = "1") @Range(min = "1", max = "100") amount: Int
    ) {
        val keys: List<String> = magenta.randomConfig.getConfig().getStringList("crates.keys")
        val randomKey = keys.random().replace("%amount%", amount.toString())
        magenta.logger.info("Hráč ${target.name} dostal náhodný klíč !")
        magenta.schedulerMagenta.doSync(magenta) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), magenta.stringUtils.magentaPlaceholders(randomKey, target))
        }
        target.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.random.crates.key.success.player"),
            Placeholder.parsed("amount", amount.toString())
        ))
        commandSender.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.random.crates.key.success.to"), TagResolver.resolver(
            Placeholder.parsed("player", target.name),
            Placeholder.parsed("amount", amount.toString())
        )))
    }

    @CommandMethod("random tag <type> <player>")
    @CommandPermission("magenta.random.tag")
    fun onRandomTag(
        commandSender: CommandSender,
        @Argument(value = "type", suggestions = "tags") type: String,
        @Argument(value = "player", suggestions = "players") target: Player
    ) {
        val tags: List<String> = magenta.randomConfig.getConfig().getStringList("tags.$type")
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