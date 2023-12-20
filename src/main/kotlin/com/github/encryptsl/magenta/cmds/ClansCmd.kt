package com.github.encryptsl.magenta.cmds

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import org.bukkit.entity.Player

@CommandDescription("Provided by plugin MagentaPro")
class ClansCmd {
    @CommandMethod("clans raid create <raid>")
    @CommandPermission("magenta.clans.create.arena")
    fun onClansCreateArena(player: Player, @Argument(value = "raid") arena: String) {

    }

    @CommandMethod("clans raid delete <raid>")
    @CommandPermission("magenta.clans.delete.raids")
    fun onClansDeleteArena(player: Player, @Argument(value = "raid", suggestions = "raids") raid: String) {

    }

    @CommandMethod("clans raid move <arena>")
    @CommandPermission("magenta.clans.move.raids")
    fun onClansMoveRaid(player: Player, @Argument(value = "raid", suggestions = "raids") raid: String) {

    }

    @CommandMethod("clans raids")
    @CommandPermission("magenta.clans.raids")
    fun onClansRaidsList(player: Player) {

    }
}