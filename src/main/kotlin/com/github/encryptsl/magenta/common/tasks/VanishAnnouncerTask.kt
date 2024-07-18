package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ActionTitleManager
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit

class VanishAnnouncerTask(
    private val magenta: Magenta
) : Runnable {
    override fun run() {
        val audience = Audience.audience(
            Bukkit.getOnlinePlayers().filter { magenta.user.getUser(it.uniqueId).isVanished() }
        )
        ActionTitleManager.sendActionBar(audience, ModernText.miniModernText(magenta.config.getString("vanish_action_bar").toString()))
    }
}