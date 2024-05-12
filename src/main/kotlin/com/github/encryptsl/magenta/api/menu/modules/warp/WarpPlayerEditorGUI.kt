package com.github.encryptsl.magenta.api.menu.modules.warp

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.HumanEntity

class WarpPlayerEditorGUI(private val magenta: Magenta) {

    enum class BUTTON_ACTION { BACK_TO_MENU, SET_WARP, SET_ICON, DELETE_HOME }

    private val menu: MenuUI by lazy { MenuUI(magenta) }
    private val simpleMenu = menu.SimpleMenu(magenta)
    private val warpPlayerGUI: WarpPlayerGUI by lazy { WarpPlayerGUI(magenta, WarpGUI(magenta), this) }

    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    private var clicked = false

    fun openWarpPlayerEditor(player: HumanEntity, warpName: String) {
        val gui = simpleMenu.simpleGui(
            ModernText.miniModernText(
                magenta.warpEditorConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("warp", warpName)
            ),
            magenta.warpEditorConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST
        )

        menu.useAllFillers(gui.filler, magenta.warpEditorConfig.getConfig())

        gui.setDefaultClickAction { el ->
            if (el.currentItem != null && el.isLeftClick || el.isRightClick) {
                simpleMenu.clickSound(el.whoClicked, magenta.warpEditorConfig.getConfig())
            }
        }

        for (el in magenta.warpEditorConfig.getConfig().getConfigurationSection("menu.items.buttons")?.getKeys(false)!!) {
            val material = Material.getMaterial(magenta.warpEditorConfig.getConfig().getString("menu.items.buttons.${el}.icon").toString()) ?: continue
            if (magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el")) {
                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.name"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                        Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                    ))

                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.slot"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                        Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                    ))

                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.icon"))
                    return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.icon",
                        Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                    ))

                val itemStack = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

                itemStack.setName(ModernText.miniModernText(magenta.warpEditorConfig.getConfig().getString("menu.items.buttons.${el}.name").toString()))

                val lores = magenta.warpEditorConfig
                    .getConfig()
                    .getStringList("menu.items.buttons.$el.lore")
                    .map { ModernText.miniModernText(it) }
                    .toMutableList()

                itemStack.addLore(lores)

                val actionItems = ItemBuilder.from(itemStack.create()).asGuiItem { action ->
                    if (action.isLeftClick) {
                        editorActionButton(player, warpName, magenta.warpEditorConfig.getConfig(), el, gui)
                    }
                }
                gui.setItem(magenta.warpEditorConfig.getConfig().getInt("menu.items.buttons.$el.slot"), actionItems)
            }
        }
        gui.open(player)
    }

    private fun editorActionButton(
        humanEntity: HumanEntity,
        warpName: String,
        config: FileConfiguration,
        el: String,
        gui: Gui
    ) {
        val action = BUTTON_ACTION.valueOf(config.getString("menu.items.buttons.$el.action").toString())

        when(action) {
            BUTTON_ACTION.BACK_TO_MENU -> {
                clicked = false
                warpPlayerGUI.openMenu(humanEntity)
            }
            BUTTON_ACTION.SET_WARP -> {
                clicked = false
                clearIcons(gui)
                magenta.warpModel.moveWarp(humanEntity.uniqueId, warpName, humanEntity.location)
                humanEntity.sendMessage(magenta.locale.translation("magenta.command.warp.success.moved", TagResolver.resolver(
                    Placeholder.parsed("warp", warpName),
                    Placeholder.parsed("x", humanEntity.location.x.toInt().toString()),
                    Placeholder.parsed("y", humanEntity.location.y.toInt().toString()),
                    Placeholder.parsed("z", humanEntity.location.z.toInt().toString())
                )))
            }
            BUTTON_ACTION.SET_ICON -> {
                loadIcons(humanEntity, gui, warpName, config)
                gui.update()
            }
            BUTTON_ACTION.DELETE_HOME -> {
                clicked = false
                magenta.homeModel.deleteHome(humanEntity.uniqueId, warpName)
                gui.close(humanEntity)
                humanEntity.sendMessage(magenta.locale.translation("magenta.command.warp.success.deleted", Placeholder.parsed("home", warpName)))
            }
        }


    }

    private fun loadIcons(
        player: HumanEntity,
        gui: Gui, warpName: String,
        fileConfiguration: FileConfiguration
    ) {
        val icons: Set<String> = fileConfiguration.getStringList("menu.icons").filter { s -> Material.getMaterial(s) != null }.map { it }.toSet()
        val itemName = fileConfiguration.getString("menu.icon.name")

        if (clicked) return
        clicked = true
        for (icon in icons) {
            val lore = fileConfiguration.getStringList("menu.icon.lore")
                .map { ModernText.miniModernText(it, Placeholder.parsed("icon", icon)) }
                .toMutableList()
            setIcon(player, warpName, itemName ?: icon, gui, icon, lore)
        }
    }

    private fun setIcon(
        player: HumanEntity,
        warpName: String,
        itemName: String,
        gui: Gui,
        materialName: String,
        lore: MutableList<Component>
    ) {
        val material = Material.getMaterial(materialName)!!
        gui.addItem(ItemBuilder.from(
            com.github.encryptsl.magenta.api.ItemBuilder(material, 1)
                .setName(
                    ModernText.miniModernText(itemName,
                        Placeholder.parsed("icon", materialName))
                ).addLore(lore)
                .create()
        ).asGuiItem { action ->
            magenta.warpModel.setWarpIcon(player.uniqueId, warpName, materialName)
            if (action.isLeftClick) {
                player.sendMessage(magenta.locale.translation("magenta.command.warp.success.change.icon", TagResolver.resolver(
                    Placeholder.parsed("warp", warpName),
                    Placeholder.parsed("icon", materialName)
                )))
            }
        })
    }

    private fun clearIcons(gui: Gui) {
        for (i in 10 .. 43) {
            if (!ignoreSlots.contains(i)) {
                gui.removeItem(i)
            }
        }
    }
}