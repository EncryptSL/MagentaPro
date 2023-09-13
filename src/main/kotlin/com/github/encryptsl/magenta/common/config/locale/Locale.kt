package com.github.encryptsl.magenta.common.config.locale

import com.github.encryptsl.magenta.Magenta
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.logging.Level

class Locale(private val magenta: Magenta) {

    private val properties = Properties()

    fun getMessage(key: String): String {
        val properties = properties.getProperty(key)
        val prefix = magenta.config.getString("prefix")
        return properties.replace("<prefix>", prefix ?: "")
    }

    fun loadLocale(configName: String) {
        try {
            val reader = InputStreamReader(FileInputStream(configName))
            properties.load(reader)
        } catch (e : IOException) {
            magenta.logger.severe( e.message ?: e.localizedMessage)
        }
    }
}