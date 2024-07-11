package com.github.encryptsl.magenta.common.database.entity

data class VotePartyEntity(val currentVotes: Int = 0, val lastVoteParty: Long?, val lastWinnerOfParty: String?)