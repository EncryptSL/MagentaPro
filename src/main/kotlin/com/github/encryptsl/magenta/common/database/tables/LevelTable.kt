package com.github.encryptsl.magenta.common.database.tables

import org.jetbrains.exposed.sql.Table

object LevelTable : Table("magenta_levels") {
    private val id = integer("id").autoIncrement()
    val username = varchar("username", 36)
    val uuid = varchar("uuid", 36)
    val level = integer("level")
    val experience = integer("experience")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}