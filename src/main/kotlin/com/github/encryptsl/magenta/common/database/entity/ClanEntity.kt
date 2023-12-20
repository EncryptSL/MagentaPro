package com.github.encryptsl.magenta.common.database.entity

data class ClanEntity(
    val clanName: String,
    val members: HashMap<String, String>,
    val rating: Int,
)