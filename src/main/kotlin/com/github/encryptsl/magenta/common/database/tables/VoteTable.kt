package com.github.encryptsl.magenta.common.database.tables

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object VoteTable : Table("votes") {
    private val id = integer( "id").autoIncrement()
    val username: Column<String> = varchar("username", 36)
    val uuid: Column<String> = varchar("uuid", 36)
    val vote: Column<Int> = integer("vote")
    val serviceName: Column<String> = varchar("serviceName", 36)
    val last_vote: Column<Instant> = timestamp("last_vote")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}