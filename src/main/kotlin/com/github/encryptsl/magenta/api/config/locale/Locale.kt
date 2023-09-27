package com.github.encryptsl.magenta.api.config.locale

import com.github.encryptsl.magenta.Magenta
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class Locale(private val magenta: Magenta) {

    private val properties = Properties()

    fun getMessage(key: String): String {
        val prefix = magenta.config.getString("prefix")
        val properties = properties.getProperty(key)
            ?: properties.getProperty("magenta.missing.translation".replace("<key>", key), "Missing Translation $key")

        return properties.replace("<prefix>", prefix ?: "")
    }

    fun loadLocale(configName: String) {
        try {
            val reader = InputStreamReader(FileInputStream(File(magenta.dataFolder, configName)))
            properties.load(reader)
        } catch (e : IOException) {
            magenta.logger.severe( e.message ?: e.localizedMessage)
        }
    }
}