package com.github.encryptsl.magenta.common.filter

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.filter.modules.AntiSpam
import com.github.encryptsl.magenta.common.filter.modules.CapsLock
import com.github.encryptsl.magenta.common.filter.modules.IPFilter
import com.github.encryptsl.magenta.common.filter.modules.Swear
import com.github.encryptsl.magenta.common.filter.modules.WebsiteFilter

class ChatChecksManager(private val magenta: Magenta) {

    fun initializeChecks() {
        AntiSpam(magenta)
        CapsLock(magenta)
        IPFilter(magenta)
        Swear(magenta)
        WebsiteFilter(magenta)
    }
}