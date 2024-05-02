package com.github.encryptsl.magenta.api.config.locale

import com.github.encryptsl.magenta.Magenta
import com.github.encryptsl.magenta.common.utils.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class Locale(private val magenta: Magenta) {

    private val properties = Properties()

    fun translation(translationKey: String)
        = ModernText.miniModernText(getMessage(translationKey))

    fun translation(translationKey: String, tagResolver: TagResolver)
        = ModernText.miniModernText(getMessage(translationKey), tagResolver)

    fun getMessage(key: String): String {
        val prefix = magenta.config.getString("prefix", "").toString()
        val properties = getProperty(key)

        return properties.replace("<prefix>", prefix)
    }

    private fun getProperty(key: String): String = properties.getProperty(key)
        ?: getProperty("magenta.missing.translation", "Missing Translation $key").replace("<key>", key)
    private fun getProperty(key: String, defValue: String) = properties.getProperty(key, defValue)

    fun loadLocale(configName: String) {
        try {
            val reader = InputStreamReader(FileInputStream(File(magenta.dataFolder, configName)))
            properties.load(reader)
        } catch (e : IOException) {
            magenta.logger.severe( e.message ?: e.localizedMessage)
        }
    }
}