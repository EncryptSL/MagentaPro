package com.github.encryptsl.magenta.common.database.tables

import org.jetbrains.exposed.sql.Table

object Warps : Table("magenta.warps") {
    private val id = integer("id").autoIncrement()
    val uuid = varchar("uuid", 36)
    val username = varchar("username", 36)
    val warpName = varchar("warp", 36)
    val world = varchar("world", 20)
    val x = long("x")
    val y = long("y")
    val z = long("z")
    val yaw = float("yaw")
    val pitch = float("pitch")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}