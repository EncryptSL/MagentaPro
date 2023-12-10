package com.github.encryptsl.magenta.common.database.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object HomeTable : Table("magenta_homes") {
    private val id = integer( "id").autoIncrement()
    val uuid: Column<String> = varchar("uuid", 36)
    val username: Column<String> = varchar("username", 36)
    val home: Column<String> = varchar("home", 36)
    val world: Column<String> = varchar("world", 20)
    val x: Column<Int> = integer("x")
    val y: Column<Int> = integer("y")
    val z: Column<Int> = integer("z")
    val yaw: Column<Float> = float("yaw")
    val pitch: Column<Float> = float("pitch")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}