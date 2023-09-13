package com.github.encryptsl.magenta.common.config

import org.bukkit.plugin.Plugin

interface Config {
    fun create(configName: String): ConfigLoader
    fun createFromResources(configName: String, plugin: Plugin): AbstractConfigLoader

    fun checkDependency(pluginName: String, plugin: Plugin): AbstractConfigLoader
}