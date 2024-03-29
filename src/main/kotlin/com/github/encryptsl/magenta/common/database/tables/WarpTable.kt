package com.github.encryptsl.magenta.common.database.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object WarpTable : Table("magenta_warps") {
    private val id = integer( "id").autoIncrement()
    val uuid: Column<String> = varchar("uuid", 36)
    val username: Column<String> = varchar("username", 36)
    val warpName: Column<String> = varchar("warp", 36)
    val warpIcon: Column<String> = varchar("warp_icon", 13).default("OAK_SIGN")
    val world: Column<String> = varchar("world", 36)
    val x: Column<Int> = integer("x")
    val y: Column<Int> = integer("y")
    val z: Column<Int> = integer("z")
    val yaw: Column<Float> = float("yaw")
    val pitch: Column<Float> = float("pitch")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}