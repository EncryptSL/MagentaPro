package com.github.encryptsl.magenta.common.database.tables

import org.jetbrains.exposed.sql.Table

object ClanTable : Table("magenta_clans") {
    private val id = integer("id").autoIncrement()
    val clanName = varchar("clan_name", 10)
    val elo = integer("clan_elo")
    val members = text("clan_members")
}