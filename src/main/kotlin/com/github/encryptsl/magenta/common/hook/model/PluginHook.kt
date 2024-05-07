package com.github.encryptsl.magenta.common.hook.model

import org.bukkit.Bukkit

abstract class PluginHook(val pluginName: String) : Hook {

    override fun isPluginEnabled(): Boolean {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null && Bukkit.getPluginManager().isPluginEnabled(pluginName)
    }

    override fun runIfElse(any: Any.() -> Unit): PluginHook {
        if(!isPluginEnabled()) {
            any.any()
        }
        return this
    }

    override fun runIfSuccess(any: Any.() -> Unit): PluginHook {
        if(isPluginEnabled()) {
            any.any()
        }
        return this
    }
}