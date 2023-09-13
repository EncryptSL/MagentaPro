package com.github.encryptsl.magenta.common.config

import com.github.encryptsl.magenta.Magenta
import java.io.File

class ConfigLoader(private val magenta: Magenta) : AbstractConfigLoader() {
    override fun create(configName: String): ConfigLoader {
        val file = File(magenta.dataFolder, configName)
        if (!file.exists()) {
            file.createNewFile()
        } else {
            magenta.logger.info("File $configName exist !")
        }
        return this
    }
}