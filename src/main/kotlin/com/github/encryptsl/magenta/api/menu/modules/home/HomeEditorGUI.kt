package com.github.encryptsl.magenta.api.menu.modules.home

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setLoreComponentList
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
import com.github.encryptsl.kmono.lib.utils.ItemCreator
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import dev.triumphteam.gui.builder.item.ItemBuilder
import dev.triumphteam.gui.guis.Gui
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class HomeEditorGUI(private val magenta: Magenta, private val homeGUI: HomeGUI) {

    enum class BUTTON_ACTION { BACK_TO_MENU, SET_HOME, SET_ICON, DELETE_HOME }

    private val menu: MenuUI by lazy { MenuUI(magenta) }
    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    private var clicked = false

    fun openHomeEditorGUI(player: Player, homeName: String) {
        val rows = magenta.homeEditorConfig.getConfig().getInt("menu.gui.size", 6)

        val gui = menu.simpleBuilderGui(rows,
            ModernText.miniModernText(
                magenta.homeEditorConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("home", homeName)
            ),
            magenta.homeEditorConfig.getConfig()
        )
        val menuSection = magenta.homeEditorConfig.getConfig().getConfigurationSection("menu.items.buttons")?.getKeys(false) ?: return

        menu.useAllFillers(gui, magenta.homeEditorConfig.getConfig())

        for (el in menuSection) {
            val material = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(
                Key.key(magenta.homeEditorConfig.getConfig().getString("menu.items.buttons.${el}.icon").toString())
            ) ?: return

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

            val itemStack = createItem(material.createItemStack()) {
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
                editorActionButton(action.whoClicked as Player, homeName, magenta.homeEditorConfig.getConfig(), el, gui)
            }
            gui.setItem(magenta.homeEditorConfig.getConfig().getInt("menu.items.buttons.$el.slot"), actionItems)
        }
        gui.open(player)
    }

    private fun editorActionButton(
        player: Player,
        warpName: String,
        config: FileConfiguration,
        el: String,
        gui: Gui
    ) {
        val action = BUTTON_ACTION.valueOf(config.getString("menu.items.buttons.$el.action").toString())

        when(action) {
            BUTTON_ACTION.BACK_TO_MENU -> {
                clicked = false
                homeGUI.openHomeGUI(player)
            }
            BUTTON_ACTION.SET_HOME -> {
                clicked = false
                clearIcons(gui)
                magenta.homeModel.moveHome(player.uniqueId, warpName, player.location)
                player.sendMessage(magenta.locale.translation("magenta.command.home.success.moved", TagResolver.resolver(
                    Placeholder.parsed("warp", warpName),
                    Placeholder.parsed("x", player.location.x.toInt().toString()),
                    Placeholder.parsed("y", player.location.y.toInt().toString()),
                    Placeholder.parsed("z", player.location.z.toInt().toString())
                )))
            }
           BUTTON_ACTION.SET_ICON -> {
               setNewIcon(gui, warpName, config)
            }
            BUTTON_ACTION.DELETE_HOME -> {
                clicked = false
                magenta.homeModel.deleteHome(player.uniqueId, warpName)
                player.closeInventory()
                player.sendMessage(magenta.locale.translation("magenta.command.warp.success.deleted", Placeholder.parsed("home", warpName)))
            }
        }
    }


    private fun setNewIcon(
        gui: Gui,
        homeName: String,
        fileConfiguration: FileConfiguration,
    ) {
        val itemName = fileConfiguration.getString("menu.icon.name")

        val icons: Set<String> = fileConfiguration.getStringList("menu.icons")
            .filter { m -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(Key.key(m)) != null }.map { it }.toSet()

        if (clicked) return

        clicked = true

        for (m in icons) {
            val lore = fileConfiguration.getStringList("menu.icon.lore")
                .map { ModernText.miniModernText(it, Placeholder.parsed("icon", m)) }
                .toMutableList()
            setIcon(homeName, gui, itemName ?: m, m, lore)
        }
    }

    private fun setIcon(
        homeName: String,
        gui: Gui,
        itemName: String,
        materialName: String,
        lore: MutableList<Component>
    ) {
        val material = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(Key.key(materialName)) ?: return
        gui.addItem(
            ItemBuilder.from(
                ItemCreator(material.createItemStack().type, 1)
                    .setName(ModernText.miniModernText(itemName,
                        Placeholder.parsed("icon", materialName))
                    ).addLore(lore)
                    .create()
            ).asGuiItem { action ->
                magenta.homeModel.setHomeIcon(action.whoClicked.uniqueId, homeName, materialName)
                action.whoClicked.sendMessage(magenta.locale.translation("magenta.command.home.success.change.icon", TagResolver.resolver(
                    Placeholder.parsed("home", homeName),
                    Placeholder.parsed("icon", materialName)
                )))
            }
        )
        gui.update()
    }

    private fun clearIcons(gui: Gui) {
        for (i in 10 .. 43) {
            if (!ignoreSlots.contains(i)) {
                gui.removeItem(i)
            }
        }
    }
}