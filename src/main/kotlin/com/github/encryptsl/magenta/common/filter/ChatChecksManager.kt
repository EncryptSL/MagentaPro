package com.github.encryptsl.magenta.common.filter

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.filter.modules.*

class ChatChecksManager(private val magenta: Magenta) {

    fun initializeChecks() {
        AntiSpam(magenta)
        CapsLock(magenta)
        IPFilter(magenta)
        Swear(magenta)
        WebsiteFilter(magenta)
    }
}