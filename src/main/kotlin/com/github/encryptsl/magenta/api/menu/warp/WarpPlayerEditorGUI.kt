package com.github.encryptsl.magenta.api.menu.warp

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.common.utils.ModernText
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

    private val menu: MenuUI by lazy { MenuUI(magenta) }
    private val warpPlayerGUI: WarpPlayerGUI by lazy { WarpPlayerGUI(magenta, WarpGUI(magenta), this) }

    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    private var clicked = false

    fun openWarpPlayerEditor(player: HumanEntity, warpName: String) {
        val gui = menu.simpleGui(
            ModernText.miniModernText(
                magenta.warpEditorConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("warp", warpName)
            ),
            magenta.warpEditorConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST
        )

        menu.useAllFillers(gui.filler, magenta.warpEditorConfig.getConfig())

        for (el in magenta.warpEditorConfig.getConfig().getConfigurationSection("menu.items.buttons")?.getKeys(false)!!) {
            val material = Material.getMaterial(magenta.warpEditorConfig.getConfig().getString("menu.items.buttons.${el}.icon").toString()) ?: continue
            if (magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el")) {
                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.name"))
                    return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.name",
                        Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                    ))

                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.slot"))
                    return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.slot",
                        Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                    ))

                if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.icon"))
                    return player.sendMessage(magenta.localeConfig.translation("magenta.menu.error.not.defined.icon",
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
                        backToMenu(player, magenta.warpEditorConfig.getConfig(), el)
                        setLocation(player, warpName, magenta.warpEditorConfig.getConfig(), el, gui)
                        setNewIcon(player, warpName, magenta.warpEditorConfig.getConfig(), el, gui)
                        deleteHome(player, warpName, magenta.warpEditorConfig.getConfig(), el, gui)
                        menu.playClickSound(action.whoClicked, magenta.warpEditorConfig.getConfig())
                    }
                }
                gui.setItem(magenta.warpEditorConfig.getConfig().getInt("menu.items.buttons.$el.slot"), actionItems)
            }
        }
        gui.open(player)
    }

    private fun backToMenu(player: HumanEntity, fileConfiguration: FileConfiguration, el: String) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("BACK_TO_MENU", true)) {
            clicked = false
            warpPlayerGUI.openMenu(player)
        }
    }

    private fun setLocation(player: HumanEntity, warpName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("SET_WARP", true)) {
            clicked = false
            clearIcons(gui)
            magenta.warpModel.moveWarp(player.uniqueId, warpName, player.location)
            player.sendMessage(magenta.localeConfig.translation("magenta.command.warp.success.moved", TagResolver.resolver(
                Placeholder.parsed("warp", warpName),
                Placeholder.parsed("x", player.location.x.toInt().toString()),
                Placeholder.parsed("y", player.location.y.toInt().toString()),
                Placeholder.parsed("z", player.location.z.toInt().toString())
            )))
        }
    }

    private fun setNewIcon(player: HumanEntity, warpName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("SET_ICON", true)) {
            loadIcons(player, gui, warpName, fileConfiguration)
            gui.update()
        }
    }

    private fun loadIcons(player: HumanEntity, gui: Gui, warpName: String, fileConfiguration: FileConfiguration) {
        val icons: List<Material> = fileConfiguration.getStringList("menu.icons").filter { s -> Material.getMaterial(s)?.name == s }.map { Material.getMaterial(it)!! }
        val itemName = fileConfiguration.getString("menu.icon.name")

        if (clicked) return
        clicked = true
        for (m in icons) {
            val lore = fileConfiguration.getStringList("menu.icon.lore")
                .map { ModernText.miniModernText(it, Placeholder.parsed("icon", m.name)) }
                .toMutableList()
            setIcon(player, warpName, itemName ?: m.name, gui, m, lore)
        }
    }

    private fun setIcon(player: HumanEntity, warpName: String, itemName: String, gui: Gui, m: Material, lore: MutableList<Component>) {
        gui.addItem(ItemBuilder.from(
            com.github.encryptsl.magenta.api.ItemBuilder(m, 1)
                .setName(
                    ModernText.miniModernText(itemName,
                        Placeholder.parsed("icon", m.name))
                ).addLore(lore)
                .create()
        ).asGuiItem { action ->
            magenta.warpModel.setWarpIcon(player.uniqueId, warpName, m.name)
            if (action.isLeftClick) {
                player.sendMessage(magenta.localeConfig.translation("magenta.command.warp.success.change.icon", TagResolver.resolver(
                    Placeholder.parsed("warp", warpName),
                    Placeholder.parsed("icon", m.name)
                )))
            }
        })
    }

    private fun deleteHome(player: HumanEntity, warpName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("DELETE_HOME", true)) {
            clicked = false
            magenta.homeModel.deleteHome(player.uniqueId, warpName)
            gui.close(player)
            player.sendMessage(magenta.localeConfig.translation("magenta.command.warp.success.deleted", Placeholder.parsed("home", warpName)))
        }
    }

    private fun clearIcons(gui: Gui) {
        for (i in 10 .. 43) {
            if (!ignoreSlots.contains(i)) {
                gui.removeItem(i)
            }
        }
    }
}