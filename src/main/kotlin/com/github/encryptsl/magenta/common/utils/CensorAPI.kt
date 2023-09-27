package com.github.encryptsl.magenta.common.utils

import com.github.encryptsl.magenta.common.extensions.compactCensoring

object CensorAPI {

    fun removeProfanity(chatMessage: String, word: String) : String {
        return chatMessage.replace(Regex(word), '*'.compactCensoring(5))
    }

}