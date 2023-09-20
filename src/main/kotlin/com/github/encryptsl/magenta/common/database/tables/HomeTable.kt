package com.github.encryptsl.magenta.common.database.tables

import org.jetbrains.exposed.sql.Table

object HomeTable : Table("magenta.homes") {
    private val id = integer("id").autoIncrement()
    val uuid = varchar("uuid", 36)
    val username = varchar("username", 36)
    val home = varchar("home", 36)
    val world = varchar("world", 20)
    val x = integer("x")
    val y = integer("y")
    val z = integer("z")
    val yaw = float("yaw")
    val pitch = float("pitch")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}