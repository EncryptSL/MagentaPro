package com.github.encryptsl.magenta.api.menu.warp

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.common.utils.ModernText
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.components.GuiType
import dev.triumphteam.gui.guis.Gui
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class WarpPlayerEditorGUI(private val magenta: Magenta) {

    private val menu: MenuUI by lazy { MenuUI(magenta) }
    private val warpPlayerGUI: WarpPlayerGUI by lazy { WarpPlayerGUI(magenta, WarpGUI(magenta), this) }

    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    fun openWarpPlayerEditor(player: Player, warpName: String) {
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
            val material = Material.getMaterial(magenta.warpEditorConfig.getConfig().getString("menu.items.buttons.${el}.icon").toString())
            if (material != null) {
                if (magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el")) {
                    if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.name"))
                        return player.sendMessage(
                            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.menu.error.not.defined.name"),
                                Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                            ))

                    if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.slot"))
                        return player.sendMessage(
                            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.menu.error.not.defined.slot"),
                                Placeholder.parsed("category", magenta.warpEditorConfig.getConfig().name)
                            ))

                    if (!magenta.warpEditorConfig.getConfig().contains("menu.items.buttons.$el.icon"))
                        return player.sendMessage(
                            ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.menu.error.not.defined.icon"),
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
                        }
                    }
                    gui.setItem(magenta.warpEditorConfig.getConfig().getInt("menu.items.buttons.$el.slot"), actionItems)
                }
            }
        }
        gui.open(player)
    }

    private fun backToMenu(player: Player, fileConfiguration: FileConfiguration, el: String) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("BACK_TO_MENU", true)) {
            warpPlayerGUI.openMenu(player)
        }
    }

    private fun setLocation(player: Player, warpName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("SET_WARP", true)) {
            clearIcons(gui)
            magenta.warpModel.moveWarp(player, warpName, player.location)
            player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.moved"),
                    TagResolver.resolver(
                        Placeholder.parsed("warp", warpName),
                        Placeholder.parsed("x", player.location.x.toInt().toString()),
                        Placeholder.parsed("y", player.location.y.toInt().toString()),
                        Placeholder.parsed("z", player.location.z.toInt().toString())
                    )
                )
            )
        }
    }

    private fun setNewIcon(player: Player, warpName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("SET_ICON", true)) {
            loadIcons(player, gui, warpName, fileConfiguration)
            gui.update()
        }
    }

    private fun loadIcons(player: Player, gui: Gui, warpName: String, fileConfiguration: FileConfiguration) {
        val icons: List<Material> = fileConfiguration.getStringList("menu.icons").filter { s -> Material.getMaterial(s)?.name == s }.map { Material.getMaterial(it)!! }
        val itemName = fileConfiguration.getString("menu.icon.name")
        icons.forEachIndexedSlots { i, m ->
            if (!ignoreSlots.contains(i)) {
                val lore = fileConfiguration.getStringList("menu.icon.lore")
                    .map { ModernText.miniModernText(it, Placeholder.parsed("icon", m.name)) }
                    .toMutableList()
                gui.setItem(i, ItemBuilder.from(
                    com.github.encryptsl.magenta.api.ItemBuilder(m, 1)
                        .setName(
                            ModernText.miniModernText(itemName ?: m.name,
                                Placeholder.parsed("icon", m.name))
                        ).addLore(lore)
                        .create()
                ).asGuiItem { action ->
                    magenta.warpModel.setWarpIcon(player, warpName, m.name)
                    if (action.isLeftClick) {
                        player.sendMessage(
                            ModernText.miniModernText(
                                magenta.localeConfig.getMessage("magenta.command.warp.success.change.icon"),
                                TagResolver.resolver(
                                    Placeholder.parsed("warp", warpName),
                                    Placeholder.parsed("icon", m.name)
                                )
                            )
                        )
                    }
                })
            }
        }
    }

    private fun deleteHome(player: Player, warpName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("DELETE_HOME", true)) {
            magenta.homeModel.deleteHome(player, warpName)
            gui.close(player)
            player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.warp.success.deleted"),
                TagResolver.resolver(Placeholder.parsed("home", warpName))))
        }
    }

    private fun clearIcons(gui: Gui) {
        for (i in 10 .. 43) {
            if (!ignoreSlots.contains(i)) {
                gui.removeItem(i)
            }
        }
    }

    private inline fun <T> Iterable<T>.forEachIndexedSlots(action: (index: Int, T) -> Unit) {
        var index = 10
        for (item in this) action(checkIndexOverflow(index++), item)
    }

    private fun checkIndexOverflow(index: Int): Int {
        return index
    }

}