package com.github.encryptsl.magenta.common.utils

import java.io.File

object FileUtil {
    fun getReadableFile(dataFolder: File, fileName: String): List<String>
            = File(dataFolder, fileName).useLines { it.toList() }
}