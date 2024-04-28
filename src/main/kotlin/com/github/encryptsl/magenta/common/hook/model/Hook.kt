package com.github.encryptsl.magenta.common.hook.model

interface Hook {
    /**
     * Method for check if plugin is installed
     * @return Boolean if is plugin Enabled or Hooked
     */
    fun isPluginEnabled(): Boolean

    fun runIfSuccess(any: Any.() -> Unit): PluginHook
    fun runIfElse(any: Any.() -> Unit): PluginHook

}