package com.github.encryptsl.magenta.api.chat

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.chat.enums.Violations
import com.github.encryptsl.magenta.common.filter.ChatPunishManager

abstract class AbstractChatFilter(magenta: Magenta, violations: Violations) : Chat {

    private val chatPunishManager = ChatPunishManager(magenta, violations)

    override fun punishAction(): ChatPunishManager {
        return chatPunishManager
    }

}