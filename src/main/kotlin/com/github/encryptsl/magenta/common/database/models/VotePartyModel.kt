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

class VotePartyModel(private val magenta: Magenta) : VotePartySQL {
    override fun createTable() {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction {
                VotePartyTable.insert {
                    it[voteParty] = "vote_party"
                    it[currentVotes] = 0
                    it[lastVoteParty] = null
                }
            }
        }
    }

    override fun updateParty() {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { VotePartyTable.update({ VotePartyTable.voteParty eq "vote_party" }) {
                it[currentVotes] = currentVotes.plus(1)
            } }
        }
    }

    override fun partyFinished(winner: String) {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { VotePartyTable.update({VotePartyTable.voteParty eq "vote_party"}) {
                it[currentVotes] = 0
                it[lastVoteParty] = Clock.System.now()
                it[lastWinnerOfParty] = winner
            } }
        }
    }

    override fun resetParty() {
        magenta.schedulerMagenta.doAsync(magenta) {
            transaction { VotePartyTable.update({ VotePartyTable.voteParty eq "vote_party" }) {
                it[currentVotes] = 0
                it[lastVoteParty] = null
                it[lastWinnerOfParty] = null
            } }
        }
    }

    override fun getExistTable(): Boolean {
        return transaction { VotePartyTable.exists() }
    }

    override fun getVoteParty(): VotePartyEntity {
        val party = transaction { VotePartyTable.selectAll().first() }

        return VotePartyEntity(party[VotePartyTable.currentVotes], party[VotePartyTable.lastVoteParty]?.toEpochMilliseconds() ?: 0L, party[VotePartyTable.lastWinnerOfParty] ?: "NEVER")
    }
}