package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity
import com.github.encryptsl.magenta.common.database.models.VotePartyModel

class MagentaVotePartyAPI(private val votePartyModel: VotePartyModel) : VotePartyAPI {
    override fun createTable() {
        if (getExistTable()) return

        votePartyModel.createTable()
    }
    override fun getExistTable(): Boolean {
        return votePartyModel.getExistTable().join()
    }
    override fun updateParty() {
        votePartyModel.updateParty()
    }

    override fun partyFinished(winner: String) {
        votePartyModel.partyFinished(winner)
    }
    override fun resetParty() {
        votePartyModel.resetParty()
    }
    override fun getVoteParty(): VotePartyEntity {
        return votePartyModel.getVoteParty().join()
    }
}