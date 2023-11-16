package com.github.encryptsl.magenta.common.utils

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import solar.squares.pixelwidth.utils.CenterAPI

object ModernText {
    private val miniMessage: MiniMessage by lazy { initMiniMessage() }

    @JvmStatic
    fun miniModernText(message: String): Component {
        return miniMessage.deserialize(convertVariables(message)).decoration(TextDecoration.ITALIC, false)
    }

    @JvmStatic
    fun miniModernText(message: String, resolver: TagResolver): Component {
        return miniMessage.deserialize(convertVariables(message), resolver).decoration(TextDecoration.ITALIC, false)
    }

    fun miniModernTextCenter(message: String): Component {
        return CenterAPI.center(miniMessage.deserialize(message))
    }

    fun miniModernTextCenter(message: String, resolver: TagResolver): Component {
        return CenterAPI.center(miniMessage.deserialize(convertVariables(message), resolver))
    }

    @JvmStatic
    fun papi(player: Player, message: String): String
    {
        return if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) PlaceholderAPI.setPlaceholders(player, message) else message
    }

    @JvmStatic
    fun <V : Any> hover(action: HoverEvent.Action<V>, value: V): HoverEvent<V> {
        return HoverEvent.hoverEvent(action, value)
    }
    @JvmStatic
    fun action(action: ClickEvent.Action, value: String): ClickEvent {
        return ClickEvent.clickEvent(action, value)
    }

    private fun initMiniMessage(): MiniMessage {
        return MiniMessage.builder()
            .strict(false)
            .tags(
                TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.clickEvent())
                    .resolver(StandardTags.decorations())
                    .resolver(StandardTags.font())
                    .resolver(StandardTags.hoverEvent())
                    .resolver(StandardTags.insertion())
                    .resolver(StandardTags.rainbow())
                    .resolver(StandardTags.newline())
                    .resolver(StandardTags.transition())
                    .resolver(StandardTags.gradient())
                    .build()
            )
            .build()
    }

    private fun convertVariables(value: String): String {
        val regex = """[{](\w+)[}]""".toRegex()
        return regex.replace(value) { matchResult ->
            "<${matchResult.groupValues[1]}>"
        }
    }
}