package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.common.utils.FileUtil
import org.bukkit.entity.Player


class Swear(private val magenta: Magenta) : Chat {
    override fun isDetected(player: Player, phrase: String): Boolean {
        if (!magenta.chatControl.getConfig().getBoolean("filters.swear.control")) return false

        if (player.hasPermission("magenta.chat.filter.bypass.swear")) return false

        return FileUtil.getReadableFile(magenta.dataFolder, "chatcontrol/swears.txt").find { phrase.matches(Regex("(.*)$it(.*)")) }.toBoolean()
    }


}