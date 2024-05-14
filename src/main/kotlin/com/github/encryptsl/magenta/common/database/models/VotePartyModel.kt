package com.github.encryptsl.magenta.common.database.models

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity
import com.github.encryptsl.magenta.common.database.sql.VotePartySQL
import com.github.encryptsl.magenta.common.database.tables.VotePartyTable
import fr.euphyllia.energie.model.SchedulerType
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
        VotePartyTable.insert {
            it[voteParty] = fieldPartyName
            it[currentVotes] = 0
            it[lastVoteParty] = null
        }
    }

    override fun updateParty() {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction { VotePartyTable.update({ VotePartyTable.voteParty eq fieldPartyName }) {
                it[currentVotes] = currentVotes.plus(1)
            } }
        }
    }

    override fun partyFinished(winner: String) {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
            transaction { VotePartyTable.update({VotePartyTable.voteParty eq fieldPartyName}) {
                it[currentVotes] = 0
                it[lastVoteParty] = Clock.System.now()
                it[lastWinnerOfParty] = winner
            } }
        }
    }

    override fun resetParty() {
        Magenta.scheduler.runTask(SchedulerType.ASYNC) {
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
        future.completeAsync { boolean }
        return future
    }

    override fun getVoteParty(): CompletableFuture<VotePartyEntity> {
        val future = CompletableFuture<VotePartyEntity>()
        val party = transaction {
            val partyData = VotePartyTable.selectAll().first()
            return@transaction VotePartyEntity(partyData[VotePartyTable.currentVotes], partyData[VotePartyTable.lastVoteParty]?.toEpochMilliseconds() ?: 0L, partyData[VotePartyTable.lastWinnerOfParty] ?: "NEVER")
        }
        future.completeAsync { party }
        return future
    }
}