package com.github.encryptsl.magenta.common.filter.modules

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.Chat
import com.github.encryptsl.magenta.common.utils.FileUtil
import org.bukkit.entity.Player


class Swear(private val magenta: Magenta) : Chat {
    override fun isDetected(player: Player, phrase: String): Boolean {
        var detected = false

        if (!magenta.chatControl.getConfig().getBoolean("filters.swear.control")) return false

        if (player.hasPermission("magenta.chat.filter.bypass.swear")) return false

        FileUtil.getReadableFile(magenta.dataFolder, "chatcontrol/swears.txt").forEach {
            if (phrase.matches(Regex("(.*)$it(.*)"))) {
                detected = true
            }
        }

        return detected
    }


}