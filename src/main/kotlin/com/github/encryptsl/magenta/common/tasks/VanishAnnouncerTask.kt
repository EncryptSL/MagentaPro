package com.github.encryptsl.magenta.common.tasks

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit

class VanishAnnouncerTask(
    private val magenta: Magenta
) : Runnable {
    override fun run() {
        Audience.audience(
            Bukkit.getOnlinePlayers().filter { magenta.user.getUser(it.uniqueId).isVanished() }
        ).sendActionBar(
            ModernText.miniModernText(magenta.config.getString("vanish_action_bar").toString())
        )
    }
}