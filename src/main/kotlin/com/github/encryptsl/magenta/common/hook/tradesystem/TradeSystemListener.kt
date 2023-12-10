package com.github.encryptsl.magenta.common.hook.tradesystem

import com.github.encryptsl.magenta.Magenta
import de.codingair.tradesystem.lib.codingapi.tools.items.XMaterial
import de.codingair.tradesystem.spigot.events.TradeIconInitializeEvent
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.EditorInfo
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.TransitionTargetEditorInfo
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.Type
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TradeSystemListener(private val magenta: Magenta) : Listener {

    @EventHandler
    fun onInit(event: TradeIconInitializeEvent) {
        event.registerIcon(
            magenta,
            CreditTradeIcon::class.java,
            EditorInfo("Credit icon",
                Type.ECONOMY,
                {de.codingair.codingapi.tools.items.ItemBuilder(XMaterial.EMERALD.parseItem())},
                false,
                "CreditLite"
            )
        )

        event.registerIcon(
            magenta,
            ShowCreditTradeIcon::class.java,
            TransitionTargetEditorInfo(
                "Credit preview icon",
                CreditTradeIcon::class.java
            )
        )
    }

}