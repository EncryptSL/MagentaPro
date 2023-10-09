package com.github.encryptsl.magenta.common.database.entity

import kotlinx.datetime.Instant
import java.util.*

data class VoteEntity(
    val username: String,
    val uuid: UUID,
    val vote: Int,
    val serviceName: String,
    val timestamp: Instant
)