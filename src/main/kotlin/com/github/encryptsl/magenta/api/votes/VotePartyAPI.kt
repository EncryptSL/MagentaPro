package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity

interface VotePartyAPI {
    fun createTable()
    fun updateParty()
    fun partyFinished(winner: String)
    fun resetParty()
    fun getExistTable(): Boolean
    fun getVoteParty(): VotePartyEntity
}