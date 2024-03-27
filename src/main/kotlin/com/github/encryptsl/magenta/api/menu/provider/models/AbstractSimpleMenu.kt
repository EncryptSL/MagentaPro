package com.github.encryptsl.magenta.api.menu.provider.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.modules.shop.helpers.ShopHelper
import com.github.encryptsl.magenta.api.menu.provider.buttons.ButtonClickSound
import com.github.encryptsl.magenta.api.menu.provider.models.components.CloseButton
import com.github.encryptsl.magenta.api.menu.provider.templates.SimpleMenu
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

abstract class AbstractSimpleMenu(magenta: Magenta) : CloseButton(magenta), SimpleMenu, ButtonClickSound {

    override fun simpleGui(title: String, size: Int, guiType: GuiType): Gui {
        return Gui.gui()
            .title(ModernText.miniModernText(title))
            .type(guiType)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }

    override fun simpleGui(title: Component, size: Int, guiType: GuiType): Gui {
        return Gui.gui()
            .title(title)
            .type(guiType)
            .rows(size)
            .disableItemPlace()
            .disableItemTake()
            .disableItemDrop()
            .disableItemSwap()
            .create()
    }
    override fun clickSound(humanEntity: HumanEntity, fileConfiguration: FileConfiguration) {
        val type = fileConfiguration.getString("menu.gui.click-sounds.ui", "ui.button.click").toString()
        ShopHelper.playSound(humanEntity, type, 5f, 1f)
    }
}