package com.github.encryptsl.magenta.api.votes

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.database.entity.VotePartyEntity
import com.github.encryptsl.magenta.common.database.models.VotePartyModel
import java.util.concurrent.CompletableFuture

class MagentaVotePartyAPI(private val votePartyModel: VotePartyModel) : VotePartyAPI {
    override fun createTable() {
        getVoteParty().thenApply {
            Magenta.instance.logger.info("VoteParty already exist !")
            return@thenApply false
        }.exceptionally {
            votePartyModel.createTable()
            Magenta.instance.logger.info("VoteParty is successfully created !")
            return@exceptionally true
        }.join()
    }
    override fun getExistTable(): Boolean {
        return votePartyModel.getExistTable().join()
    }
    override fun updateParty(vote: Int) {
        votePartyModel.updateParty(vote)
    }

    override fun partyFinished(winner: String) {
        votePartyModel.partyFinished(winner)
    }
    override fun resetParty() {
        votePartyModel.resetParty()
    }
    override fun getVoteParty(): CompletableFuture<VotePartyEntity> {
        return votePartyModel.getVoteParty()
    }
}