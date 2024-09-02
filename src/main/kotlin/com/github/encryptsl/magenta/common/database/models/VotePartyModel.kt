package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity
import com.github.encryptsl.magenta.common.database.sql.VotePartySQL
import com.github.encryptsl.magenta.common.database.tables.VotePartyTable
import kotlinx.datetime.Clock
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture

class VotePartyModel : VotePartySQL {

    private val fieldPartyName = "vote_party"

    override fun createTable() {
        transaction {
            VotePartyTable.insert {
                it[voteParty] = fieldPartyName
                it[currentVotes] = 0
                it[lastVoteParty] = null
            }
        }
    }

    override fun updateParty(vote: Int) {
        CompletableFuture.runAsync {
            transaction { VotePartyTable.update({ VotePartyTable.voteParty eq fieldPartyName }) {
                it[currentVotes] = currentVotes.plus(vote)
            } }
        }
    }

    override fun partyFinished(winner: String) {
        CompletableFuture.runAsync {
            transaction { VotePartyTable.update({VotePartyTable.voteParty eq fieldPartyName}) {
                it[currentVotes] = 0
                it[lastVoteParty] = Clock.System.now()
                it[lastWinnerOfParty] = winner
            } }
        }
    }

    override fun resetParty() {
        CompletableFuture.runAsync {
            transaction { VotePartyTable.update({ VotePartyTable.voteParty eq fieldPartyName }) {
                it[currentVotes] = 0
                it[lastVoteParty] = null
                it[lastWinnerOfParty] = null
            } }
        }
    }

    override fun getExistTable(): CompletableFuture<Boolean> {
        val future: CompletableFuture<Boolean> = CompletableFuture.supplyAsync {
            transaction { VotePartyTable.exists() }
        }
        return future
    }

    override fun getVoteParty(): CompletableFuture<VotePartyEntity> {
        val future: CompletableFuture<VotePartyEntity> = CompletableFuture.supplyAsync {
            transaction {
                try {
                    val partyData = VotePartyTable.selectAll().where { VotePartyTable.voteParty eq fieldPartyName }.first()
                    VotePartyEntity(partyData[VotePartyTable.currentVotes], partyData[VotePartyTable.lastVoteParty]?.toEpochMilliseconds() ?: 0L, partyData[VotePartyTable.lastWinnerOfParty] ?: "NEVER")
                } catch (e : ExposedSQLException) {
                    throw RuntimeException("Vote Party not created or table is empty :'( !")
                }
            }
        }
        return future
    }
}