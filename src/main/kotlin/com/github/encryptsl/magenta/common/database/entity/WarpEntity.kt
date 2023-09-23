package com.github.encryptsl.magenta.common.database.entity

data class WarpEntity(
    val owner: String,
    val uuid: String,
    val warpName: String,
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val pitch: Float,
    val yaw: Float
)