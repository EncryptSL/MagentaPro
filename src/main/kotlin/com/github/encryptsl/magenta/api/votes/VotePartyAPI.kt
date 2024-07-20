package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity
import java.util.concurrent.CompletableFuture

interface VotePartyAPI {
    fun createTable()
    fun updateParty(vote: Int = 1)
    fun partyFinished(winner: String)
    fun resetParty()
    fun getExistTable(): Boolean
    fun getVoteParty(): CompletableFuture<VotePartyEntity>
}