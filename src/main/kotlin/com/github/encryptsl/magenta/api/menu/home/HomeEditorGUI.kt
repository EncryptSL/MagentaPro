package com.github.encryptsl.magenta.api.menu.home

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

class HomeEditorGUI(private val magenta: Magenta, private val homeGUI: HomeGUI) {

    private val menu: MenuUI by lazy { MenuUI(magenta) }

    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    fun openHomeEditorGUI(player: Player, homeName: String) {
        val gui = menu.simpleGui(
            ModernText.miniModernText(
                magenta.homeEditorConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("home", homeName)
            ),
            magenta.homeEditorConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST
        )

        menu.useAllFillers(gui.filler, magenta.homeEditorConfig.getConfig())

        for (el in magenta.homeEditorConfig.getConfig().getConfigurationSection("menu.items.buttons")?.getKeys(false)!!) {
            val material = Material.getMaterial(magenta.homeEditorConfig.getConfig().getString("menu.items.buttons.${el}.icon").toString())

            if (material != null) {
                if (magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el")) {
                    if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.name"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.menu.error.not.defined.name"),
                            Placeholder.parsed("category", magenta.homeEditorConfig.getConfig().name)
                        ))

                    if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.slot"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.menu.error.not.defined.slot"),
                            Placeholder.parsed("category", magenta.homeEditorConfig.getConfig().name)
                        ))

                    if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.icon"))
                        return player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.menu.error.not.defined.icon"),
                            Placeholder.parsed("category", magenta.homeEditorConfig.getConfig().name)
                        ))

                    val itemStack = com.github.encryptsl.magenta.api.ItemBuilder(material, 1)

                    itemStack.setName(ModernText.miniModernText(magenta.homeEditorConfig.getConfig().getString("menu.items.buttons.${el}.name").toString()))

                    val lores = magenta.warpEditorConfig
                        .getConfig()
                        .getStringList("menu.items.buttons.$el.lore")
                        .map { ModernText.miniModernText(it) }
                        .toMutableList()

                    itemStack.addLore(lores)

                    val actionItems = ItemBuilder.from(itemStack.create()).asGuiItem { action ->
                        if (action.isLeftClick) {
                            backToMenu(player, magenta.homeEditorConfig.getConfig(), el)
                            setLocation(player, homeName, magenta.homeEditorConfig.getConfig(), el, gui)
                            setNewIcon(player, homeName, magenta.homeEditorConfig.getConfig(), el, gui)
                            deleteHome(player, homeName, magenta.homeEditorConfig.getConfig(), el, gui)
                        }
                    }
                    gui.setItem(magenta.homeEditorConfig.getConfig().getInt("menu.items.buttons.$el.slot"), actionItems)
                }
            }
            gui.open(player)
        }
    }

    private fun backToMenu(player: Player, fileConfiguration: FileConfiguration, el: String) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("BACK_TO_MENU", true)) {
            homeGUI.openHomeGUI(player)
        }
    }

    private fun setLocation(player: Player, homeName: String,fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("SET_HOME", true)) {
            clearIcons(gui)
            magenta.homeModel.moveHome(player, homeName, player.location)
            player.sendMessage(
                ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.moved"),
                    TagResolver.resolver(
                        Placeholder.parsed("home", homeName),
                        Placeholder.parsed("x", player.location.x.toInt().toString()),
                        Placeholder.parsed("y", player.location.y.toInt().toString()),
                        Placeholder.parsed("z", player.location.z.toInt().toString())
                    )
                )
            )
        }
    }

    private fun setNewIcon(player: Player, homeName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        val itemName = fileConfiguration.getString("menu.icon.name")

        val icons: List<Material> = fileConfiguration.getStringList("menu.icons").filter { m -> Material.getMaterial(m)?.name != null }.map { Material.getMaterial(it)!! }

        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("SET_ICON", true)) {
            icons.forEachIndexedSlots { i, m ->
                val lore = fileConfiguration.getStringList("menu.icon.lore")
                    .map { ModernText.miniModernText(it, Placeholder.parsed("icon", m.name)) }
                    .toMutableList()
                if (!ignoreSlots.contains(i)) {
                    gui.setItem(i, ItemBuilder.from(
                        com.github.encryptsl.magenta.api.ItemBuilder(m, 1)
                            .setName(ModernText.miniModernText(itemName ?: m.name,
                                Placeholder.parsed("icon", m.name))
                            ).addLore(lore)
                            .create()
                    ).asGuiItem { action ->
                        if (action.isLeftClick) {
                            magenta.homeModel.setHomeIcon(player, homeName, m.name)
                            player.sendMessage(
                                ModernText.miniModernText(
                                    magenta.localeConfig.getMessage("magenta.command.home.success.change.icon"),
                                    TagResolver.resolver(
                                        Placeholder.parsed("home", homeName),
                                        Placeholder.parsed("icon", m.name)
                                    )
                                )
                            )
                        }
                    })
                }
            }
            gui.update()
        }
    }

    private fun deleteHome(player: Player, homeName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("DELETE_HOME", true)) {
            magenta.homeModel.deleteHome(player, homeName)
            gui.close(player)
            player.sendMessage(ModernText.miniModernText(magenta.localeConfig.getMessage("magenta.command.home.success.deleted"),
                TagResolver.resolver(Placeholder.parsed("home", homeName))))
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