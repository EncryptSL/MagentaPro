package com.github.encryptsl.magenta.api.menu.modules.home

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.extensions.createItem
import com.github.encryptsl.kmono.lib.extensions.meta
import com.github.encryptsl.kmono.lib.extensions.setLoreComponentList
import com.github.encryptsl.kmono.lib.extensions.setNameComponent
import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.api.menu.MenuUI
import com.github.encryptsl.magenta.api.menu.modules.warp.WarpPlayerEditorGUI.BUTTON_ACTION
import dev.triumphteam.gui.container.GuiContainer
import dev.triumphteam.gui.layout.BoxGuiLayout
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import dev.triumphteam.gui.slot.Slot
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class HomeEditorGUI(private val magenta: Magenta, private val homeGUI: HomeGUI) {

    enum class BUTTON_ACTION { BACK_TO_MENU, SET_HOME, SET_ICON, DELETE_HOME }

    private val menu: MenuUI by lazy { MenuUI(magenta) }
    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    private var clicked = false

    fun openHomeEditorGUI(player: Player, homeName: String) {
        val rows = magenta.homeEditorConfig.getConfig().getInt("menu.gui.size", 6)

        val gui = Gui.of(rows).title(
            ModernText.miniModernText(
                magenta.homeEditorConfig.getConfig().getString("menu.gui.display").toString(),
                Placeholder.parsed("home", homeName)
            )
        )

        val menuSection = magenta.homeEditorConfig.getConfig().getConfigurationSection("menu.items.buttons")?.getKeys(false) ?: return

        gui.component { component ->
            component.render { container, viewer ->
                menu.useAllFillers(rows, container, magenta.homeEditorConfig.getConfig())

                for (el in menuSection.withIndex()) {
                    val material = Material.getMaterial(magenta.homeEditorConfig.getConfig().getString("menu.items.buttons.${el}.icon").toString()) ?: continue
                    if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el")) continue

                    if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.name"))
                        return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.name",
                            Placeholder.parsed("category", magenta.homeEditorConfig.getConfig().name)
                        ))

                    if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.slot"))
                        return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.slot",
                            Placeholder.parsed("category", magenta.homeEditorConfig.getConfig().name)
                        ))

                    if (!magenta.homeEditorConfig.getConfig().contains("menu.items.buttons.$el.icon"))
                        return@render viewer.sendMessage(magenta.locale.translation("magenta.menu.error.not.defined.icon",
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

                    val actionItems = ItemBuilder.from(itemStack).asGuiItem { whoClicked, _ ->
                        editorActionButton(whoClicked, homeName, magenta.homeEditorConfig.getConfig(), el.value, container)
                    }
                    container.set(magenta.homeEditorConfig.getConfig().getInt("menu.items.buttons.$el.slot"), actionItems)
                }
            }
        }.build().open(player)
    }

    private fun editorActionButton(
        player: Player,
        warpName: String,
        config: FileConfiguration,
        el: String,
        container: GuiContainer<Player, ItemStack>
    ) {
        val action = BUTTON_ACTION.valueOf(config.getString("menu.items.buttons.$el.action").toString())

        when(action) {
            BUTTON_ACTION.BACK_TO_MENU -> {
                clicked = false
                homeGUI.openHomeGUI(player)
            }
            BUTTON_ACTION.SET_HOME -> {
                clicked = false
                //clearIcons(gui)
                magenta.homeModel.moveHome(player.uniqueId, warpName, player.location)
                player.sendMessage(magenta.locale.translation("magenta.command.home.success.moved", TagResolver.resolver(
                    Placeholder.parsed("warp", warpName),
                    Placeholder.parsed("x", player.location.x.toInt().toString()),
                    Placeholder.parsed("y", player.location.y.toInt().toString()),
                    Placeholder.parsed("z", player.location.z.toInt().toString())
                )))
            }
           BUTTON_ACTION.SET_ICON -> {
               clicked = true
               setNewIcon(container, warpName, config)
                //gui.update()
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
        container: GuiContainer<Player, ItemStack>,
        homeName: String,
        fileConfiguration: FileConfiguration,
    ) {
        val itemName = fileConfiguration.getString("menu.icon.name")

        val icons: Set<String> = fileConfiguration.getStringList("menu.icons").filter { m -> Material.getMaterial(m) != null }.map { it }.toSet()

        if (clicked) return

        for (m in icons) {
            val lore = fileConfiguration.getStringList("menu.icon.lore")
                .map { ModernText.miniModernText(it, Placeholder.parsed("icon", m)) }
                .toMutableList()
            setIcon(homeName, container, itemName ?: m, m, lore)
        }
    }

    private fun setIcon(
        homeName: String,
        container: GuiContainer<Player, ItemStack>,
        itemName: String,
        materialName: String,
        lore: MutableList<Component>
    ) {
        val material = Material.getMaterial(materialName)!!

        for (slot in BoxGuiLayout(Slot.of(3, 1), Slot.of(4, 9)).generatePositions()) {
            container.set(slot, ItemBuilder.from(
                com.github.encryptsl.kmono.lib.utils.ItemBuilder(material, 1)
                    .setName(ModernText.miniModernText(itemName,
                        Placeholder.parsed("icon", materialName))
                    ).addLore(lore)
                    .create()
            ).asGuiItem { whoClicked, _ ->
                magenta.homeModel.setHomeIcon(whoClicked.uniqueId, homeName, materialName)
                whoClicked.sendMessage(magenta.locale.translation("magenta.command.home.success.change.icon", TagResolver.resolver(
                    Placeholder.parsed("home", homeName),
                    Placeholder.parsed("icon", materialName)
                )))
            })
        }
    }

    //private fun clearIcons(gui: Gui) {
    //    for (i in 10 .. 43) {
    //        if (!ignoreSlots.contains(i)) {
    //            gui.removeItem(i)
    //        }
    //    }
    //}
}