package com.github.encryptsl.magenta.common.database.entity

import java.util.*

data class HomeEntity(
    val owner: String,
    val uuid: UUID,
    val homeName: String,
    val homeIcon: String,
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val pitch: Float,
    val yaw: Float
)