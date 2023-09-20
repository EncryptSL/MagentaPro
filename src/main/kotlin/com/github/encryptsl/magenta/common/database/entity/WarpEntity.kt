package com.github.encryptsl.magenta.common.database.entity

data class WarpEntity(
    val owner: String,
    val homeName: String,
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val pitch: Float,
    val yaw: Float
)