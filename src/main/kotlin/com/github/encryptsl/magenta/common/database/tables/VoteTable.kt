package com.github.encryptsl.magenta.common.database.tables

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object VoteTable : Table() {
    private val id = integer( "id").autoIncrement()
    val username: Column<String> = varchar("username", 36)
    val uuid: Column<String> = varchar("uuid", 36)
    val vote: Column<Int> = integer("vote")
    val serviceName: Column<String> = varchar("serviceName", 36)
    val timestamp: Column<Instant> = timestamp("timestamp")

    override val primaryKey: PrimaryKey = PrimaryKey(id)

    override val tableName: String
        get() = "votes"
}