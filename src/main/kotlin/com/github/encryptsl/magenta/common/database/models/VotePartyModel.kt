package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity
import com.github.encryptsl.magenta.common.database.sql.VotePartySQL
import com.github.encryptsl.magenta.common.database.tables.VotePartyTable
import kotlinx.datetime.Clock
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
        Magenta.scheduler.impl.runAsync {
            transaction { VotePartyTable.update({ VotePartyTable.voteParty eq fieldPartyName }) {
                it[currentVotes] = currentVotes.plus(vote)
            } }
        }
    }

    override fun partyFinished(winner: String) {
        Magenta.scheduler.impl.runAsync {
            transaction { VotePartyTable.update({VotePartyTable.voteParty eq fieldPartyName}) {
                it[currentVotes] = 0
                it[lastVoteParty] = Clock.System.now()
                it[lastWinnerOfParty] = winner
            } }
        }
    }

    override fun resetParty() {
        Magenta.scheduler.impl.runAsync {
            transaction { VotePartyTable.update({ VotePartyTable.voteParty eq fieldPartyName }) {
                it[currentVotes] = 0
                it[lastVoteParty] = null
                it[lastWinnerOfParty] = null
            } }
        }
    }

    override fun getExistTable(): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val boolean = transaction { VotePartyTable.exists() }
        return future.completeAsync { boolean }
    }

    override fun getVoteParty(): CompletableFuture<VotePartyEntity> {
        val future = CompletableFuture<VotePartyEntity>()
        transaction {
            val partyData = VotePartyTable.selectAll().where { VotePartyTable.voteParty eq fieldPartyName }.firstOrNull()
            if (partyData == null) {
                future.completeExceptionally(Exception("Vote Party not created or table is empty :'( !"))
            } else {
                future.completeAsync { VotePartyEntity(partyData[VotePartyTable.currentVotes], partyData[VotePartyTable.lastVoteParty]?.toEpochMilliseconds() ?: 0L, partyData[VotePartyTable.lastWinnerOfParty] ?: "NEVER") }
            }
        }
        return future
    }
}