package com.github.encryptsl.magenta.common.extensions

import java.util.UUID

fun Char.compactCensoring(count: Int): String = this.toString().repeat(count)

fun trimUUID(uuid: UUID) = uuid.toString().replace("-", "")