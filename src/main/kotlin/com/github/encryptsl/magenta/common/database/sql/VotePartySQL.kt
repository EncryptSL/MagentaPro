package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity
import java.util.concurrent.CompletableFuture

interface VotePartySQL {
    fun createTable()
    fun updateParty(vote: Int = 1)
    fun partyFinished(winner: String)
    fun resetParty()
    fun getExistTable(): CompletableFuture<Boolean>
    fun getVoteParty(): CompletableFuture<VotePartyEntity>
}