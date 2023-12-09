package com.github.encryptsl.magenta.common.database.sql

import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity

interface VotePartySQL {
    fun createTable()
    fun updateParty()
    fun partyFinished(winner: String)
    fun resetParty()
    fun getExistTable(): Boolean
    fun getVoteParty(): VotePartyEntity
}