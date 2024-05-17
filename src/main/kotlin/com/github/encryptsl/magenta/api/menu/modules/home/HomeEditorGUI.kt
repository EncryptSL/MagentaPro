package com.github.encryptsl.magenta.api.menu.modules.home

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setLoreComponentList
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
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
import org.bukkit.entity.Player

class HomeEditorGUI(private val magenta: Magenta, private val homeGUI: HomeGUI) {

    private val menu: MenuUI by lazy { MenuUI(magenta) }
    private val simpleMenu = menu.SimpleMenu(magenta)

    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    private var clicked = false

    fun openHomeEditorGUI(player: Player, homeName: String) {
        val gui = simpleMenu.simpleGui(
            ModernText.miniModernText(
                magenta.homeEditorConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("home", homeName)
            ),
            magenta.homeEditorConfig.getConfig().getInt("menu.gui.size", 6),
            GuiType.CHEST
        )

        menu.useAllFillers(gui.filler, magenta.homeEditorConfig.getConfig())

        val menuSection = magenta.homeEditorConfig.getConfig().getConfigurationSection("menu.items.buttons")?.getKeys(false) ?: return

        for (el in menuSection) {
            val material = Material.getMaterial(magenta.homeEditorConfig.getConfig().getString("menu.items.buttons.${el}.icon").toString()) ?: continue
            if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el")) continue

            if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.name"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                            Placeholder.parsed("category", magenta.homeEditorConfig.getConfig().name)
                ))

            if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.slot"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                    Placeholder.parsed("category", magenta.homeEditorConfig.getConfig().name)
                ))

            if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.icon"))
                return player.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.icon",
                    Placeholder.parsed("category", magenta.homeEditorConfig.getConfig().name)
                ))

            val itemStack = createItem(material) {
                amount = 1
                meta {
                    setNameComponent = ModernText.miniModernText(magenta.homeEditorConfig.getConfig().getString("menu.items.buttons.${el}.name").toString())
                    setLoreComponentList = magenta.warpEditorConfig
                        .getConfig()
                        .getStringList("menu.items.buttons.$el.lore")
                        .map { ModernText.miniModernText(it) }

                }
            }

            val actionItems = ItemBuilder.from(itemStack).asGuiItem { action ->
                if (action.isLeftClick) {
                    backToMenu(player, magenta.homeEditorConfig.getConfig(), el)
                    setLocation(player, homeName, magenta.homeEditorConfig.getConfig(), el, gui)
                    setNewIcon(player, homeName, magenta.homeEditorConfig.getConfig(), el, gui)
                    deleteHome(player, homeName, magenta.homeEditorConfig.getConfig(), el, gui)
                    simpleMenu.clickSound(player, magenta.homeEditorConfig.getConfig())
                }
            }
            gui.setItem(magenta.homeEditorConfig.getConfig().getInt("menu.items.buttons.$el.slot"), actionItems)
            gui.open(player)
        }
    }

    private fun backToMenu(player: Player, fileConfiguration: FileConfiguration, el: String) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("BACK_TO_MENU", true)) {
            clicked = false
            homeGUI.openHomeGUI(player)
        }
    }

    private fun setLocation(player: Player, homeName: String,fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("SET_HOME", true)) {
            clicked =false
            clearIcons(gui)
            magenta.homeModel.moveHome(player.uniqueId, homeName, player.location)
            player.sendMessage(
               magenta.locale.translation("magenta.command.home.success.moved",
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

    private fun setNewIcon(player: HumanEntity, homeName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        val itemName = fileConfiguration.getString("menu.icon.name")

        val icons: Set<String> = fileConfiguration.getStringList("menu.icons").filter { m -> Material.getMaterial(m) != null }.map { it }.toSet()

        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("SET_ICON", true)) {
            if (clicked) return
            clicked = true

            for (m in icons) {
                val lore = fileConfiguration.getStringList("menu.icon.lore")
                    .map { ModernText.miniModernText(it, Placeholder.parsed("icon", m)) }
                    .toMutableList()
                setIcon(player, homeName, gui, itemName ?: m, m, lore)
            }
            gui.update()
        }
    }

    private fun setIcon(player: HumanEntity, homeName: String, gui: Gui, itemName: String, materialName: String, lore: MutableList<Component>) {
        val material = Material.getMaterial(materialName)!!
        gui.addItem(ItemBuilder.from(
            com.github.encryptsl.kmono.lib.utils.ItemBuilder(material, 1)
                .setName(ModernText.miniModernText(itemName,
                    Placeholder.parsed("icon", materialName))
                ).addLore(lore)
                .create()
        ).asGuiItem { action ->
            if (action.isLeftClick) {
                magenta.homeModel.setHomeIcon(player.uniqueId, homeName, materialName)
                player.sendMessage(magenta.locale.translation("magenta.command.home.success.change.icon", TagResolver.resolver(
                    Placeholder.parsed("home", homeName),
                    Placeholder.parsed("icon", materialName)
                )))
            }
        })
    }

    private fun deleteHome(player: HumanEntity, homeName: String, fileConfiguration: FileConfiguration, el: String, gui: Gui) {
        if (fileConfiguration.getString("menu.items.buttons.$el.action").equals("DELETE_HOME", true)) {
            clicked = false
            magenta.homeModel.deleteHome(player.uniqueId, homeName)
            gui.close(player)
            player.sendMessage(magenta.locale.translation("magenta.command.home.success.deleted", Placeholder.parsed("home", homeName)))
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