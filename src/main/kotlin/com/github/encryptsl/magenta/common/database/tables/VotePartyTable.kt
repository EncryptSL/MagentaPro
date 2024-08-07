package com.github.encryptsl.magenta.common.database.tables

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object VotePartyTable : Table("magenta_voteparty") {
    private val id = integer("id").autoIncrement()
    val voteParty = varchar("party_name", 16).uniqueIndex()
    val currentVotes = integer("current_votes")
    val lastVoteParty: Column<Instant?> = timestamp("last_party").nullable()
    val lastWinnerOfParty = varchar("last_winner", 16).nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}